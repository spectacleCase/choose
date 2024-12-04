import tactics.Food;
import tactics.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 基于上下文推荐
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/14 下午8:51
 */
public class ContextRecommender {

    // 存储用户信息的Map
    private Map<String, User> users;
    // 存储食物信息的List
    private List<Food> foods;
    // 不同天气对不同食物类别的权重
    private Map<String, Double> contextWeights;

    public ContextRecommender() {
        users = new HashMap<>();
        foods = new ArrayList<>();
        contextWeights = new HashMap<>();
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public void addFood(Food food) {
        foods.add(food);
    }

    public void setContextWeight(String weather, String foodType, double weight) {
        contextWeights.put(weather + ":" + foodType, weight);
    }

    /**
     * todo 推荐方法
     * @param userId 用户id
     * @param context 上下文
     * @param topN 个数
     * @return 推荐列表
     */
    public List<Food> recommendFoods(String userId, tactics.Context context, int topN) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户未找到");
        }

        Map<Food, Double> scores = new HashMap<>();
        for (Food food : foods) {
            // 获取用户对食物类别的偏好
            double userPreference = user.getPreference(food.getType());
            // 获取当前天气对食物类别的权重
            double contextWeight = contextWeights.getOrDefault(context. getWeather()+ ":" + food.getType(), 1.0);
            // 计算食物得分
            double score = userPreference * contextWeight;
            scores.put(food, score);
        }

        return scores.entrySet().stream()
                .sorted(Map.Entry.<Food, Double>comparingByValue().reversed()) // 按得分从高到低排序
                .limit(topN) // 取前topN个食物
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        ContextRecommender recommender = new ContextRecommender();

        // 案例描述：
        // 我们有一个用户 "user1"，他对不同食物类别的偏好如下：
        // - 火锅：0.8
        // - 沙拉：0.1
        // - 冰淇淋：0.78
        // 我们有一些食物，分别是：
        // - 火锅1（火锅）
        // - 沙拉1（沙拉）
        // - 冰淇淋1（冰淇淋）
        // - 火锅2（火锅）
        // - 沙拉2（沙拉）
        // 不同天气对不同食物类别的权重如下：
        // - 晴天：火锅 1.2，沙拉 0.8，冰淇淋 0.5
        // - 雨天：火锅 1.0，沙拉 1.2，冰淇淋 0.8
        // - 雪天：火锅 0.8，沙拉 1.0，冰淇淋 1.2
        // 我们希望在晴天时，为用户 "user1" 推荐3种食物。

        // 添加用户
        User user1 = new User("user1");
        user1.setPreference("火锅", 0.6);
        user1.setPreference("沙拉", 0.1);
        user1.setPreference("冰淇淋", 0.78);
        recommender.addUser(user1);

        // 添加食物
        recommender.addFood(new Food("火锅1", "火锅"));
        recommender.addFood(new Food("沙拉1", "沙拉"));
        recommender.addFood(new Food("冰淇淋1", "冰淇淋"));
        recommender.addFood(new Food("冰淇淋2", "冰淇淋"));
        recommender.addFood(new Food("冰淇淋3", "冰淇淋"));
        recommender.addFood(new Food("冰淇淋4", "冰淇淋"));
        recommender.addFood(new Food("火锅2", "火锅"));
        recommender.addFood(new Food("沙拉2", "沙拉"));

        // 设置上下文权重
        recommender.setContextWeight("晴天", "火锅", 0.5);
        recommender.setContextWeight("晴天", "沙拉", 0.8);
        recommender.setContextWeight("晴天", "冰淇淋", 0.5);
        recommender.setContextWeight("雨天", "火锅", 1.0);
        recommender.setContextWeight("雨天", "沙拉", 1.2);
        recommender.setContextWeight("雨天", "冰淇淋", 0.8);
        recommender.setContextWeight("雪天", "火锅", 2.0);
        recommender.setContextWeight("雪天", "沙拉", 1.0);
        recommender.setContextWeight("雪天", "冰淇淋", 1.2);

        // 生成推荐
        tactics.Context context = new tactics.Context("雪天");
        List<Food> recommendations = recommender.recommendFoods("user1", context, 3);
        System.out.println("推荐的食物: " + recommendations.stream().map(food -> food.getId()).toList());
    }
}
