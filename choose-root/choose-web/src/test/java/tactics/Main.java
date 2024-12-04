package tactics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // 初始化数据
        Map<String, User> users = new HashMap<>();
        List<Food> foods = new ArrayList<>();
        Map<String, Double> contextWeights = new HashMap<>();
        Map<String, Map<String, Double>> userRatings = new HashMap<>();
        Map<String, Map<String, Double>> itemSimilarities = new HashMap<>();

        // 添加用户
        User user1 = new User("user1");
        user1.setPreference("火锅", 0.6);
        user1.setPreference("沙拉", 0.1);
        user1.setPreference("冰淇淋", 0.78);
        users.put(user1.id, user1);

        // 添加食物
        foods.add(new Food("火锅1", "火锅"));
        foods.add(new Food("沙拉1", "沙拉"));
        foods.add(new Food("冰淇淋1", "冰淇淋"));
        foods.add(new Food("冰淇淋2", "冰淇淋"));
        foods.add(new Food("冰淇淋3", "冰淇淋"));
        foods.add(new Food("冰淇淋4", "冰淇淋"));
        foods.add(new Food("火锅2", "火锅"));
        foods.add(new Food("沙拉2", "沙拉"));

        // 设置上下文权重
        contextWeights.put("晴天:火锅", 0.5);
        contextWeights.put("晴天:沙拉", 0.8);
        contextWeights.put("晴天:冰淇淋", 0.5);
        contextWeights.put("雨天:火锅", 1.0);
        contextWeights.put("雨天:沙拉", 1.2);
        contextWeights.put("雨天:冰淇淋", 0.8);
        contextWeights.put("雪天:火锅", 2.0);
        contextWeights.put("雪天:沙拉", 1.0);
        contextWeights.put("雪天:冰淇淋", 1.2);

        // 添加用户评分
        userRatings.put("user1", new HashMap<String, Double>() {{
            put("火锅1", 5.0);
            put("沙拉1", 3.0);
            put("冰淇淋1", 4.0);
        }});
        userRatings.put("user2", new HashMap<String, Double>() {{
            put("火锅1", 3.0);
            put("沙拉1", 4.0);
            put("冰淇淋2", 4.5);
        }});
        userRatings.put("user3", new HashMap<String, Double>() {{
            put("沙拉1", 2.0);
            put("冰淇淋1", 3.5);
            put("冰淇淋2", 4.0);
        }});

        // 计算物品之间的相似度
        CascadeHybridRecommendationStrategy cascadeStrategy = new CascadeHybridRecommendationStrategy(userRatings, itemSimilarities);
        cascadeStrategy.calculateItemSimilarities();

        // 创建推荐系统并设置策略
        RecommenderSystem recommenderSystem = new RecommenderSystem(cascadeStrategy);

        // 推荐物品
        List<String> recommendations = recommenderSystem.recommendItems("user1", 2);
        System.out.println("推荐美食: " + recommendations);

        // 切换策略为基于上下文的推荐
        Context context = new Context("雪天");
        ContextRecommendationStrategy contextStrategy = new ContextRecommendationStrategy(users, foods, contextWeights, context);
        recommenderSystem.setStrategy(contextStrategy);

        // 推荐食物
        recommendations = recommenderSystem.recommendItems("user1", 3);
        System.out.println("推荐美食: " + recommendations);
    }
}