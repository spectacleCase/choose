import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于用户之间的推荐
 */
public class UserBasedCollaborativeFiltering {

    // 存储用户评分的Map
    private Map<String, Map<String, Double>> userRatings;

    public UserBasedCollaborativeFiltering() {
        userRatings = new HashMap<>();
    }

    public void addUserRating(String userId, String itemId, double rating) {
        userRatings.computeIfAbsent(userId, k -> new HashMap<>()).put(itemId, rating);
    }

    // 计算用户之间的余弦相似度
    private double cosineSimilarity(Map<String, Double> user1Ratings, Map<String, Double> user2Ratings) {
        Set<String> commonItems = new HashSet<>(user1Ratings.keySet());
        commonItems.retainAll(user2Ratings.keySet());

        if (commonItems.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String itemId : commonItems) {
            double rating1 = user1Ratings.get(itemId);
            double rating2 = user2Ratings.get(itemId);
            dotProduct += rating1 * rating2;
            norm1 += rating1 * rating1;
            norm2 += rating2 * rating2;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // 找到与目标用户最相似的K个用户
    private List<String> findSimilarUsers(String targetUserId, int k) {
        Map<String, Double> targetUserRatings = userRatings.get(targetUserId);
        if (targetUserRatings == null) {
            throw new IllegalArgumentException("目标用户未找到");
        }

        Map<String, Double> similarities = new HashMap<>();
        for (Map.Entry<String, Map<String, Double>> entry : userRatings.entrySet()) {
            String userId = entry.getKey();
            if (!userId.equals(targetUserId)) {
                double similarity = cosineSimilarity(targetUserRatings, entry.getValue());
                similarities.put(userId, similarity);
            }
        }

        return similarities.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 推荐物品
    public List<String> recommendItems(String targetUserId, int k) {
        List<String> similarUsers = findSimilarUsers(targetUserId, k);
        Map<String, Double> itemScores = new HashMap<>();
        Map<String, Double> targetUserRatings = this.userRatings.get(targetUserId);

        for (String userId : similarUsers) {
            Map<String, Double> userRatings = this.userRatings.get(userId);
            for (Map.Entry<String, Double> entry : userRatings.entrySet()) {
                String itemId = entry.getKey();
                double rating = entry.getValue();
                if (!targetUserRatings.containsKey(itemId)) {
                    itemScores.put(itemId, itemScores.getOrDefault(itemId, 0.0) + rating);
                }
            }
        }

        return itemScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        UserBasedCollaborativeFiltering recommender = new UserBasedCollaborativeFiltering();

        // 添加用户评分
        recommender.addUserRating("user1", "物品1", 5.0);
        recommender.addUserRating("user1", "物品2", 3.0);
        recommender.addUserRating("user1", "物品3", 4.0);

        recommender.addUserRating("user2", "物品1", 3.0);
        recommender.addUserRating("user2", "物品2", 4.0);
        recommender.addUserRating("user2", "物品4", 4.5);

        recommender.addUserRating("user3", "物品2", 2.0);
        recommender.addUserRating("user3", "物品3", 3.5);
        recommender.addUserRating("user3", "物品4", 4.0);

        // 推荐物品
        System.out.println("给user1推荐的东西");
        List<String> recommendations = recommender.recommendItems("user1", 2);
        System.out.println("推荐物品: " + recommendations);
    }
}