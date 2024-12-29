package com.choose.service.recommend.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.choose.dishes.pojos.Dishes;
import com.choose.dishes.pojos.Shops;
import com.choose.mapper.RecommendMapper;
import com.choose.mapper.TagMapper;
import com.choose.recommoend.pojos.Recommend;
import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.recommend.RecommendationStrategy;
import com.choose.tag.pojos.Tag;
import com.choose.tag.pojos.TagAssociation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 基于内容+用户协同过滤的级联混合推荐策略
 * 数据量可能oom， redis
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/14 下午10:17
 */
@Service
@Slf4j
public class CascadeHybridRecommendationStrategy extends RecommendationStrategy {



    // 用户行为数据（是否点过某道菜），使用线程安全的 ConcurrentHashMap
    private final Map<String, Set<String>> USER_BEHAVIOR = new ConcurrentHashMap<>();

    /**
     * 初始化用户行为数据
     */
    @PostConstruct
    public void initUserBehavior() {
        loadUserBehavior();
    }

    /**
     * 每天凌晨 12 点重新加载用户行为数据
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨 12 点执行
    public void reloadUserBehavior() {
        loadUserBehavior();
    }

    /**
     * 从数据库加载用户行为数据
     */
    private void loadUserBehavior() {
        // 清空原有数据
        USER_BEHAVIOR.clear();

        // 分页加载数据
        int pageSize = 1000;
        int page = 0;
        while (true) {
            Page<Recommend> recommendPage = recommendMapper.selectPage(
                    new Page<>(page, pageSize),
                    new LambdaQueryWrapper<Recommend>().eq(Recommend::getIsSuccess, 1)
            );
            List<Recommend> recommends = recommendPage.getRecords();

            // 如果数据为空，退出循环
            if (recommends.isEmpty()) {
                break;
            }

            // 处理当前批次的数据
            for (Recommend recommend : recommends) {
                String userId = String.valueOf(recommend.getUserId());
                String dishesId = String.valueOf(recommend.getDishesId());
                USER_BEHAVIOR.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet())
                        .add(dishesId);
            }

            // 下一页
            page++;
        }

        log.info("用户行为数据已重新加载，当前数据量：{}", USER_BEHAVIOR.size());
    }

    @Override
    public List<RecommendVo> recommendItems(String targetUserId, int k) {
        // 计算用户相似度
        Map<String, Double> similarities = calculateSimilarities(targetUserId);
        List<String> recommendations = generateRecommendations(targetUserId, similarities, k);
        return convertToRecommendVo(targetUserId, recommendations);
    }

    private List<RecommendVo> convertToRecommendVo(String targetUserId, List<String> recommendations) {
        // 一次性查询所有菜品
        List<Dishes> dishesList = dishesMapper.selectList(new LambdaQueryWrapper<Dishes>().in(Dishes::getId, recommendations));
        Set<Long> shopIds = dishesList.stream()
                .map(Dishes::getShop)
                .collect(Collectors.toSet());

        Map<Long, Shops> shopsMap = shopsMapper.selectBatchIds(shopIds)
                .stream()
                .collect(Collectors.toMap(Shops::getId, Function.identity()));
        List<TagAssociation> tagAssociations = tagAssociationMapper.selectList(
                new LambdaQueryWrapper<TagAssociation>().in(TagAssociation::getModelId, shopIds)
        );
        Set<Long> tagIds = tagAssociations.stream()
                .map(TagAssociation::getTagId)
                .collect(Collectors.toSet());

        Map<Long, Tag> tagsMap = super.tagMapper.selectBatchIds(tagIds)
                .stream()
                .collect(Collectors.toMap(Tag::getId, Function.identity()));
        Map<Long, List<Tag>> shopTagsMap = tagAssociations.stream()
                .collect(Collectors.groupingBy(
                        TagAssociation::getModelId,
                        Collectors.mapping(ta -> tagsMap.get(ta.getTagId()), Collectors.toList())
                ));

        // 构建推荐结果
        List<RecommendVo> recommendVoList = new ArrayList<>();
        for (Dishes dishes : dishesList) {
            // 创建推荐记录
            Recommend recommend = new Recommend();
            recommend.setUserId(Long.valueOf(targetUserId));
            recommend.setDishesId(dishes.getId());
            recommend.setDescription("好吃");
            recommendMapper.insert(recommend);

            // 构建 RecommendVo
            RecommendVo recommendVo = new RecommendVo();
            BeanUtils.copyProperties(dishes, recommendVo);
            BeanUtils.copyProperties(recommend, recommendVo);
            recommendVo.setId(String.valueOf(recommend.getId()));
            recommendVo.setShopId(String.valueOf(dishes.getShop()));

            // 设置店铺信息
            Shops shops = shopsMap.get(dishes.getShop());
            if (shops != null) {
                recommendVo.setShopName(shops.getShopName());
                recommendVo.setCoordinate(shops.getCoordinate());
            }

            // 设置标签信息
            List<Tag> tags = shopTagsMap.get(dishes.getShop());
            if (tags != null) {
                List<String> tagNames = tags.stream()
                        .map(Tag::getTag)
                        .collect(Collectors.toList());
                recommendVo.setTagName(tagNames);
            }

            recommendVo.setAiDescription(recommend.getDescription());
            recommendVoList.add(recommendVo);
        }

        return recommendVoList;
    }

    // 计算用户相似度（Jaccard 相似度）
    private double jaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        // 交集
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        // 并集
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    // 计算目标用户与其他用户的相似度
    private Map<String, Double> calculateSimilarities(String targetUser) {
        Map<String, Double> similarities = new HashMap<>();
        Set<String> targetUserBehavior = USER_BEHAVIOR.get(targetUser);

        for (Map.Entry<String, Set<String>> entry : USER_BEHAVIOR.entrySet()) {
            String user = entry.getKey();
            if (!user.equals(targetUser)) {
                double similarity = jaccardSimilarity(targetUserBehavior, entry.getValue());
                similarities.put(user, similarity);
            }
        }
        return similarities;
    }

    // 生成推荐记录
    private List<String> generateRecommendations(String targetUser, Map<String, Double> similarities, int k) {
        Map<String, Double> recommendationScores = new HashMap<>();

        // 计算推荐分数
        for (Map.Entry<String, Double> entry : similarities.entrySet()) {
            String user = entry.getKey();
            double similarity = entry.getValue();
            Set<String> items = USER_BEHAVIOR.get(user);
            for (String item : items) {
                // 如果目标用户没有点过该菜品
                if (!USER_BEHAVIOR.get(targetUser).contains(item)) {
                    recommendationScores.put(item, recommendationScores.getOrDefault(item, 0.0) + similarity);
                }
            }
        }

        // 按推荐分数排序
        List<String> recommendations = new ArrayList<>(recommendationScores.keySet());
        recommendations.sort((a, b) -> Double.compare(recommendationScores.get(b), recommendationScores.get(a)));

        // 如果推荐结果不足 k 个，从用户点过的美食中随机选取补足
        if (recommendations.size() < k) {
            Set<String> userItems = USER_BEHAVIOR.get(targetUser);
            List<String> userItemList = new ArrayList<>(userItems);
            Collections.shuffle(userItemList);

            // 从用户点过的美食中选取补足
            int remaining = k - recommendations.size();
            for (int i = 0; i < remaining && i < userItemList.size(); i++) {
                recommendations.add(userItemList.get(i));
            }
        }

        // 截取前 k 个推荐结果
        if (recommendations.size() > k) {
            recommendations = recommendations.subList(0, k);
        }

        return recommendations;
    }
}
