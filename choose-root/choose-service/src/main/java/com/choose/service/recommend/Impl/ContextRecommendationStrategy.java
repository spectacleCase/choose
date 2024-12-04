package com.choose.service.recommend.Impl;

import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.recommend.RecommendationStrategy;
import com.choose.service.recommend.pojo.Context;
import com.choose.service.recommend.pojo.Food;
import com.choose.service.recommend.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 上下文推荐策略
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/14 下午10:37
 */
@Service
@Slf4j
public class ContextRecommendationStrategy extends RecommendationStrategy {

    // 用户列表
    private Map<String, User> users;
    // 食物列表
    private List<Food> foods;
    // 上下文比重
    private Map<String, Double> contextWeights;
    // 上下文情况
    private Context context;

    public ContextRecommendationStrategy() {}

    public ContextRecommendationStrategy(Map<String, User> users, List<Food> foods, Map<String, Double> contextWeights, Context context) {
        this.users = users;
        this.foods = foods;
        this.contextWeights = contextWeights;
        this.context = context;
    }

    @Override
    public List<RecommendVo> recommendItems(String targetUserId, int k) {
        User user = users.get(targetUserId);
        if(Objects.nonNull(user)) {
            Map<Food, Double> scores = new HashMap<>();
            for (Food food : foods) {
                // 获取用户对食物类别的偏好
                double userPreference = user.getPreference(food.getType());
                // 获取当前天气对食物类别的权重
                Class<? extends Context> contextClass = context.getClass();
                Field[] fields = contextClass.getDeclaredFields();
                double score = 0;
                try{
                    for (Field field : fields) {
                        field.setAccessible(true);
                        Object fieldValue = field.get(context);
                        score += userPreference * contextWeights.getOrDefault(fieldValue + ":" + food.getType(), 1.0);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                scores.put(food, score);
            }
            // 按得分从高到低排序 -> 取前k个食物
            List<String> collect = scores.entrySet().stream()
                    .sorted(Map.Entry.<Food, Double>comparingByValue().reversed())
                    .limit(k)
                    .map(entry -> entry.getKey().getId())
                    .toList();
            return dishesMapper.getRecommendVosByIds(collect);
        }
        throw new IllegalArgumentException("用户未找到");
    }
}





