package com.choose.service.ranking.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.choose.annotation.SysLog;
import com.choose.common.MathUtils;
import com.choose.constant.CommonConstants;
import com.choose.dishes.pojos.Dishes;
import com.choose.dishes.pojos.Mark;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.josn.JsonUtil;
import com.choose.mapper.*;
import com.choose.ranking.dto.RankingDto;
import com.choose.ranking.pojos.Column;
import com.choose.ranking.pojos.Ranking;
import com.choose.ranking.vo.RankingVo;
import com.choose.redis.utils.RedisUtils;
import com.choose.service.aiModel.DeepSeekV3Service;
import com.choose.service.aiModel.ModelServiceFactory;
import com.choose.service.dishes.DishesService;
import com.choose.service.ranking.RankingService;
import com.choose.user.pojos.Review;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/5 上午1:23
 */
@Slf4j
@Service
public class RankingServiceImpl extends ServiceImpl<RankingMapper, Ranking> implements RankingService {

    @Resource
    private ColumnMapper columnMapper;

    @Resource
    private RankingMapper rankingMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private DishesMapper dishesMapper;

    @Resource
    private MarkMapper markMapper;

    @Resource
    private DishesService dishesService;

    @Resource
    private ReviewMapper reviewMapper;

    @Resource
    private ModelServiceFactory modelServiceFactory;


    /**
     * 获取栏目
     */
    @SysLog("获取栏目")
    @Override
    public List<Column> getColumn() {
        return columnMapper.selectList(null);
    }

    /**
     * 根据id获取排行榜
     */
    @Override
    @SysLog("获取排行榜")
    public List<RankingVo> getRanking(RankingDto dto) {
        List<RankingVo> instance = getRedisRankingVos(dto);
        if (Objects.nonNull(instance)) {
            return instance;
        }
        try {
            ReentrantLock reentrantLock = new ReentrantLock();
            if (reentrantLock.tryLock()) {
                Long l = rankingMapper.selectCount(null);
                if (l != 0 && (l / CommonConstants.Common.PANE_SIZE) < dto.getPage()) {
                    dto.setPage((int) ((l / CommonConstants.Common.PANE_SIZE)));
                }

                Page<Ranking> page = new Page<>(dto.getPage(), CommonConstants.Common.PANE_SIZE);
                QueryWrapper<Ranking> rankingQueryWrapper = new QueryWrapper<>();
                rankingQueryWrapper.lambda()
                        .eq(Ranking::getColumnId, dto.getId())
                        .orderByDesc(Ranking::getMark);
                Page<Ranking> rankingPage = rankingMapper.selectPage(page, rankingQueryWrapper);
                List<Ranking> rankingList = rankingPage.getRecords();
                if (Objects.isNull(rankingList) || rankingList.isEmpty()) {
                    reentrantLock.unlock();
                    return null;
                }
                QueryWrapper<Dishes> dishesQueryWrapper = new QueryWrapper<>();
                rankingList.forEach(ranking -> dishesQueryWrapper.lambda()
                        .eq(Dishes::getId, ranking.getModelId())
                        .or());
                dishesQueryWrapper.lambda().orderByDesc(Dishes::getMark);
                List<Dishes> dishes = dishesMapper.selectList(dishesQueryWrapper);
                List<RankingVo> rankingVoList = new ArrayList<>();
                dishes.forEach(item -> {
                    RankingVo rankingVo = RankingVo.builder()
                            .dishesName(item.getDishesName())
                            .image(item.getImage())
                            .mark(item.getMark())
                            .build();
                    rankingVoList.add(rankingVo);
                });
                String rankingJson = JsonUtil.toJson(rankingVoList);
                // 缓存并释放锁
                redisUtils.set(CommonConstants.RedisKeyPrefix.RANGING_COLUMN_KEY + dto.getId() + ":"
                        + dto.getPage(), rankingJson);
                reentrantLock.unlock();
                return rankingVoList;
            } else {
                // 获取锁失败 等待100毫秒再查询redis
                Thread.sleep(100);
                instance = getRedisRankingVos(dto);
                if (Objects.nonNull(instance)) {
                    return instance;
                }
            }
        } catch (Exception e) {
            log.error("redis ranking error", e);
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }

        return null;
    }

    private @Nullable List<RankingVo> getRedisRankingVos(RankingDto dto) {
        Object rankingRedis = redisUtils.get(CommonConstants.RedisKeyPrefix.RANGING_COLUMN_KEY + ":"
                + dto.getId() + dto.getPage());
        if (Objects.nonNull(rankingRedis)) {
            List<RankingVo> instance = null;
            try {
                instance = JsonUtil.toList((String) rankingRedis, RankingVo.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (Objects.nonNull(instance)) {
                return instance;
            }
        }
        return null;
    }

    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "0 */1 * * * ?")
    @Transactional
    public void refreshRanking() {
        log.info("计算全部菜品的分数--定时任务");
        refreshDishesAverage();
        log.info("未来数据定时刷新--定时任务");
        List<Column> columns = columnMapper.selectList(null);
        List<Ranking> rankingList = new ArrayList<>();

        // 根据栏目查询对应的前50对应的评分
        for (Column column : columns) {
            Page<Dishes> dishesPage = new Page<>(1, CommonConstants.Common.PANE_MAX);
            QueryWrapper<Dishes> dishesQueryWrapper = new QueryWrapper<>();
            dishesQueryWrapper
                    .lambda()
                    .eq(Dishes::getColumnId, column.getId())
                    .orderByDesc(Dishes::getMark);
            List<Dishes> records = dishesMapper.selectPage(dishesPage, dishesQueryWrapper).getRecords();
            LambdaQueryWrapper<Ranking> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Ranking::getColumnId, column.getId());
            rankingMapper.delete(queryWrapper);

            // 创建新的排名数据
            for (Dishes dishes : records) {
                Ranking ranking = new Ranking();
                ranking.setMark(dishes.getMark());
                ranking.setColumnId(dishes.getColumnId());
                ranking.setModelId(dishes.getId());
                rankingList.add(ranking);
            }
        }
        saveBatch(rankingList);
        refreshRedisRanking(rankingList);
    }

    /**
     * 将栏目+菜品评分排名 以每10个放入redis
     */
    public void refreshRedisRanking(List<Ranking> rankingList) {
        if (Objects.isNull(rankingList) || rankingList.isEmpty()) {
            return;
        }
        Map<Long, List<Ranking>> rankingMap = rankingList.stream()
                .collect(Collectors.groupingBy(Ranking::getColumnId));
        for (Map.Entry<Long, List<Ranking>> entry : rankingMap.entrySet()) {
            Long columnId = entry.getKey();
            List<Ranking> rankings = entry.getValue();
            int page = 1;
            List<RankingVo> rankingVoList = new ArrayList<>();

            for (Ranking ranking : rankings) {
                Dishes dishes = dishesMapper.selectById(ranking.getModelId());
                RankingVo rankingVo = RankingVo.builder()
                        .mark(ranking.getMark())
                        .dishesName(dishes.getDishesName())
                        .image(dishes.getImage())
                        .build();
                rankingVoList.add(rankingVo);

                // 每10个元素存入Redis
                if (rankingVoList.size() == 10) {
                    String json = JsonUtil.toJson(rankingVoList);
                    redisUtils.set(CommonConstants.RedisKeyPrefix.RANGING_COLUMN_KEY + columnId + ":" + page, json);
                    page++;
                    rankingVoList = new ArrayList<>();
                }
            }
            if (!rankingVoList.isEmpty()) {
                String json = JsonUtil.toJson(rankingVoList);
                redisUtils.set(CommonConstants.RedisKeyPrefix.RANGING_COLUMN_KEY + columnId + ":" + page, json);
            }
        }
    }

    /**
     * 计算菜品的综合评分
     */
    public void refreshDishesAverage() {
        List<Dishes> dishes = dishesMapper.selectList(null);
        for (Dishes item : dishes) {
            LambdaQueryWrapper<Mark> eq = new QueryWrapper<Mark>().lambda().eq(Mark::getDishesId, item.getId());
            List<Mark> marks = markMapper.selectList(eq);
            List<Review> reviews = reviewMapper.selectList(Wrappers.<Review>lambdaQuery()
                    .eq(Review::getDishesId, item.getId()));
            Object[] reviewArray = reviews.stream().map(Review::getReview).toArray();
            int[] ava = marks.stream().mapToInt(Mark::getMark).toArray();
            double average = MathUtils.average(ava);
            if (reviewArray.length == 0) {
                continue;
            }
            double score = 0;
            try {
                // score = MathUtils.score(average, TestTextAbstract.reviewRating(ArrayUtil.toString(reviewArray)));
                score = MathUtils.score(average, (Double) modelServiceFactory.getService("deepSeekV3").process(DeepSeekV3Service.functionName.REVIEW_RATING));
            } catch (Exception e) {
                throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
            }
            item.setMark(score);
        }
        dishesService.updateBatchById(dishes);
    }
}
