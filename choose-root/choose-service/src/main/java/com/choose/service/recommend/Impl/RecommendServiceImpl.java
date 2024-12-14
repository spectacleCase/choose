package com.choose.service.recommend.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.choose.annotation.SysLog;
import com.choose.common.CommentPageDto;
import com.choose.common.WeatherTemperatureWind;
import com.choose.config.UserLocalThread;
import com.choose.constant.CommonConstants;
import com.choose.dishes.pojos.Dishes;
import com.choose.dishes.pojos.Shops;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.mapper.ColumnMapper;
import com.choose.mapper.DishesMapper;
import com.choose.mapper.RecommendMapper;
import com.choose.mapper.ShopsMapper;
import com.choose.ranking.pojos.Column;
import com.choose.recommoend.dto.RecommendDto;
import com.choose.recommoend.pojos.Recommend;
import com.choose.recommoend.vo.RecommendListVo;
import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.recommend.RecommendService;
import com.choose.service.recommend.RecommenderSystem;
import com.choose.service.tag.TagAssociationService;
import com.choose.service.tag.TagService;
import com.choose.tag.pojos.Tag;
import com.choose.tag.pojos.TagAssociation;
import com.choose.user.pojos.UserInfo;
import com.choose.utils.common.CommonUtils;
import com.choose.utils.common.WeatherTemperatureWindUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
    private DishesMapper dishesMapper;

    @Resource
    private TagAssociationService tagAssociationService;

    @Resource
    private ColumnMapper columnMapper;

    @Resource
    private ShopsMapper shopsMapper;

    @Resource
    private TagService tagService;

    @Resource
    private RandomRecommend randomRecommend;

    @Resource
    private ContextRecommendationStrategy crs;


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

    @Override
    public List<RecommendVo> recommendPlus(int num) {
        List<RecommendVo> recommendVos = new ArrayList<>();
        List<String> paths = new ArrayList<>();

        UserInfo user = UserLocalThread.getUser();
        if (Objects.nonNull(user)) {
            List<TagAssociation> list = tagAssociationService.list(Wrappers.<TagAssociation>lambdaQuery()
                    .eq(TagAssociation::getModelId, user.getId()));
            if (Objects.isNull(list) || list.isEmpty()) {
                // 跳过标签
                HashMap<String, String> weather = CommonUtils.getWeather();
                /* 测试使用 */
//                weather = new HashMap<>();
                /* 测试使用 */
                if (Objects.isNull(weather) || weather.isEmpty()) {
                    // 获取天气失败
                    List<Column> columns = columnMapper.selectList(null);

                    Column column = columns.get(new Random().nextInt(columns.size()));
                    log.info(column.toString());
                    for (int i = 0; i < num; i++) {
                        RecommendVo recommendVo = new RecommendVo();
                        List<Dishes> dishes = dishesMapper.selectList(Wrappers.<Dishes>lambdaQuery()
                                .eq(Dishes::getColumnId, column.getId()));
                        /* 测试使用 */
                        if (Objects.isNull(dishes) || dishes.isEmpty()) {
                            continue;
                        }
                        /* 测试使用 */
                        Shops shops = shopsMapper.selectOne(Wrappers.<Shops>lambdaQuery()
                                .eq(Shops::getId, dishes.get(i).getShop()));
                        List<Tag> tagsByModelId = tagAssociationService.getTagsByModelId(dishes.get(i).getId());
                        BeanUtils.copyProperties(dishes.get(i), recommendVo);
                        BeanUtils.copyProperties(shops, recommendVo);
                        recommendVo.setShopId(String.valueOf(shops.getId()));
                        recommendVo.setTagName(tagsByModelId.stream().map(Tag::getTag).toList());
                        paths.add(shops.getCoordinate());
                        recommendVos.add(recommendVo);
                    }
                } else {
                    // todo 待改
                    // 获取天气成功
                    String s = weather.get("windpower");
                    if (s.contains("3")) {
                        s = "3";
                    }

                    Map<String, WeatherTemperatureWind> weatherTemperatureWind = WeatherTemperatureWindUtils.getWeatherTemperatureWind(weather.get("weather"),
                            Integer.parseInt(weather.get("temperature")), Integer.parseInt(s));
                    ArrayList<String> strings = new ArrayList<>();
                    weatherTemperatureWind.forEach((k, v) -> {
                        strings.add(v.getTag());
                    });
                    List<Tag> taglist = tagService.list(Wrappers.<Tag>lambdaQuery().in(Tag::getTag, strings));
                    // 得到标签
                    List<TagAssociation> tagAssociationList = tagAssociationService.list(Wrappers.<TagAssociation>lambdaQuery()
                            .in(TagAssociation::getTagId, taglist.stream().map(Tag::getId).toList()));
                    List<Long> longList = tagAssociationList.stream().map(TagAssociation::getModelId).toList();
                    List<Dishes> dishes = dishesMapper.selectList(Wrappers.<Dishes>lambdaQuery()
                            .in(Dishes::getId, longList)
                            .orderByDesc(Dishes::getMark));
                    dishes.forEach(d -> {
                        Shops shops = shopsMapper.selectOne(Wrappers.<Shops>lambdaQuery()
                                .eq(Shops::getId, d.getShop()));
                        RecommendVo recommendVo = new RecommendVo();
                        BeanUtils.copyProperties(d, recommendVo);
                        BeanUtils.copyProperties(shops, recommendVo);
                        recommendVo.setShopId(String.valueOf(shops.getId()));
                        recommendVo.setTagName(taglist.stream().map(Tag::getTag).toList());
                        paths.add(shops.getCoordinate());
                        recommendVos.add(recommendVo);
                    });
                }
            } else {
                // 选择标签
                return null;
            }

            // 路径计算 - todo 待用
            String[] pathPlanning = CommonUtils.pathPlanning("110.922615,21.681233", paths);

            // 得出结果
            Recommend recommend = new Recommend();
            recommend.setUserId(Long.valueOf(UserLocalThread.getUser().getId()));
            recommend.setDishesId(Long.valueOf(recommendVos.get(0).getId()));
            recommend.setDescription("好吃");
            recommendMapper.insert(recommend);
            return recommendVos;
        } else {
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }

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
            // 你本身，最近一个人，
            //
            recommenderSystem.setStrategy(randomRecommend);
            // if(dto.getNum() > 3) {
            //     // 随机
            //     recommenderSystem.setStrategy(randomRecommend);
            // } else {
            //     recommenderSystem.setStrategy(crs);
            // }
            recommendVos = recommenderSystem.recommendItems(user.getId(),dto.getNum());

        } else {
            throw new RuntimeException("用户未登录");
        }
        return recommendVos;
    }
}

