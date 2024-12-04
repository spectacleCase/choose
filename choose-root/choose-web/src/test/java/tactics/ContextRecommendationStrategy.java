package tactics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContextRecommendationStrategy implements RecommendationStrategy {

    // 用户列表
    private Map<String, User> users;
    // 食物列表
    private List<Food> foods;
    // 上下文比重
    private Map<String, Double> contextWeights;
    // 上下文情况
    private Context context;

    public ContextRecommendationStrategy(Map<String, User> users, List<Food> foods, Map<String, Double> contextWeights, Context context) {
        this.users = users;
        this.foods = foods;
        this.contextWeights = contextWeights;
        this.context = context;
    }

    @Override
    public List<String> recommendItems(String userId, int topN) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户未找到");
        }

        Map<Food, Double> scores = new HashMap<>();
        for (Food food : foods) {
            // 获取用户对食物类别的偏好
            double userPreference = user.getPreference(food.type);
            // 获取当前天气对食物类别的权重
            double contextWeight = contextWeights.getOrDefault(context.weather + ":" + food.type, 1.0);
            // 计算食物得分
            double score = userPreference * contextWeight;
            scores.put(food, score);
        }
        List<String> collect = scores.entrySet().stream()
                .sorted(Map.Entry.<Food, Double>comparingByValue().reversed()) // 按得分从高到低排序
                .limit(topN) // 取前topN个食物
                .map(entry -> entry.getKey().id)
                .collect(Collectors.toList());

        return collect;
    }
}