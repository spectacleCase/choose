package com.choose.service.recommend.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.choose.annotation.SysLog;
import com.choose.common.dto.CommentPageDto;
import com.choose.config.UserLocalThread;
import com.choose.constant.CommonConstants;
import com.choose.mapper.RecommendMapper;
import com.choose.recommoend.dto.RecommendDto;
import com.choose.recommoend.pojos.Recommend;
import com.choose.recommoend.vo.RecommendListVo;
import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.recommend.RecommendService;
import com.choose.service.recommend.RecommenderSystem;
import com.choose.user.pojos.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 当新用户进来时，由于没有历史记录，结合用户反馈标签，首选热门热门项目推荐，和基于内容推荐
 * 热门项目推荐：当用户没有标签时，则推荐随机一个栏目最热的菜肴给他，同时配合天气加上路径，寻找最短且符合天气的
 * 基于内容推荐：在全部的菜肴中，寻找一个匹配度和用户匹配度最高的可选菜品，
 * 上述的推荐得出结果都要再包含天气和路径的影响最终得出结果。
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/3 下午1:47
 */
@Service
@Slf4j
public class RecommendServiceImpl implements RecommendService {

    @Resource
    private RecommendMapper recommendMapper;

    @Resource
    private RandomRecommend randomRecommend;

    @Resource
    private ContextRecommendationStrategy crs;

    @Resource
    private CascadeHybridRecommendationStrategy cr;


    /**
     * 推荐记录
     */
    @Override
    @SysLog("推荐记录")
    public List<RecommendListVo> recommendRecord(CommentPageDto dto) {
        UserInfo user = UserLocalThread.getUser();
        if (Objects.nonNull(user)) {
            Long maxPage =recommendMapper.selectCount(new QueryWrapper<>());
            if ((maxPage / CommonConstants.Common.PANE_SIZE) < dto.getPage()) {
                return new ArrayList<RecommendListVo>();
                // 分页兜底
                // dto.setPage((int) (maxPage / CommonConstants.Common.PANE_SIZE));
            }
            return  recommendMapper.getRecommendRecordList(Long.valueOf(user.getId()),dto.getPage(),CommonConstants.Common.PANE_SIZE);
        }
        throw new RuntimeException("请登录");
    }


    /**
     * 生产版推荐菜品
     * @param dto - 推荐结果个数
     */
    @Override
    @SysLog("推荐记录")
    public List<RecommendVo> recommendMinus(RecommendDto dto) {
        RecommenderSystem recommenderSystem = new RecommenderSystem();
        UserInfo user = UserLocalThread.getUser();
        List<RecommendVo> recommendVos;
        if(Objects.nonNull(user)) {
            try{
                if(dto.getNum() == 5) {
                    recommenderSystem.setStrategy(randomRecommend);
                } else if(dto.getNum() == 3) {
                    recommenderSystem.setStrategy(cr);
                } else {
                    recommenderSystem.setStrategy(crs);
                }
            } catch (Exception e) {
                recommenderSystem.setStrategy(randomRecommend);
            }
            recommendVos = recommenderSystem.recommendItems(user.getId(),dto.getNum());

        } else {
            throw new RuntimeException("用户未登录");
        }
        return recommendVos;
    }

    @Override
    public void recommendSuccess(String id) {
        LambdaUpdateWrapper<Recommend> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Recommend::getId, id)
                .set(Recommend::getIsSuccess,1);
        recommendMapper.update(updateWrapper);
    }
}

