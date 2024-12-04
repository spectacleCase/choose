import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于物品之间的推荐
 */
public class ItemBasedCollaborativeFiltering {

    // 存储用户评分的Map
    private Map<String, Map<String, Double>> userRatings;
    // 存储物品相似度的Map
    private Map<String, Map<String, Double>> itemSimilarities;

    public ItemBasedCollaborativeFiltering() {
        userRatings = new HashMap<>();
        itemSimilarities = new HashMap<>();
    }

    public void addUserRating(String userId, String itemId, double rating) {
        userRatings.computeIfAbsent(userId, k -> new HashMap<>()).put(itemId, rating);
    }

    // 计算物品之间的余弦相似度
    private double cosineSimilarity(Map<String, Double> item1Ratings, Map<String, Double> item2Ratings) {
        Set<String> commonUsers = new HashSet<>(item1Ratings.keySet());
        commonUsers.retainAll(item2Ratings.keySet());

        if (commonUsers.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String userId : commonUsers) {
            double rating1 = item1Ratings.get(userId);
            double rating2 = item2Ratings.get(userId);
            dotProduct += rating1 * rating2;
            norm1 += rating1 * rating1;
            norm2 += rating2 * rating2;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // 计算所有物品之间的相似度
    public void calculateItemSimilarities() {
        Map<String, Map<String, Double>> itemRatings = new HashMap<>();

        // 将用户评分数据转换为物品评分数据
        for (Map.Entry<String, Map<String, Double>> entry : userRatings.entrySet()) {
            String userId = entry.getKey();
            Map<String, Double> ratings = entry.getValue();
            for (Map.Entry<String, Double> ratingEntry : ratings.entrySet()) {
                String itemId = ratingEntry.getKey();
                double rating = ratingEntry.getValue();
                itemRatings.computeIfAbsent(itemId, k -> new HashMap<>()).put(userId, rating);
            }
        }

        // 计算物品之间的相似度
        for (String itemId1 : itemRatings.keySet()) {
            for (String itemId2 : itemRatings.keySet()) {
                if (!itemId1.equals(itemId2)) {
                    double similarity = cosineSimilarity(itemRatings.get(itemId1), itemRatings.get(itemId2));
                    itemSimilarities.computeIfAbsent(itemId1, k -> new HashMap<>()).put(itemId2, similarity);
                }
            }
        }
    }

    // 推荐物品
    public List<String> recommendItems(String targetUserId, int k) {
        Map<String, Double> targetUserRatings = userRatings.get(targetUserId);
        if (targetUserRatings == null) {
            throw new IllegalArgumentException("目标用户未找到");
        }

        Map<String, Double> itemScores = new HashMap<>();

        for (String itemId : itemSimilarities.keySet()) {
            if (!targetUserRatings.containsKey(itemId)) {
                double score = 0.0;
                for (Map.Entry<String, Double> entry : itemSimilarities.get(itemId).entrySet()) {
                    String similarItemId = entry.getKey();
                    double similarity = entry.getValue();
                    if (targetUserRatings.containsKey(similarItemId)) {
                        score += similarity * targetUserRatings.get(similarItemId);
                    }
                }
                itemScores.put(itemId, score);
            }
        }

        return itemScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        ItemBasedCollaborativeFiltering recommender = new ItemBasedCollaborativeFiltering();

        // 添加用户评分
        recommender.addUserRating("user1", "item1", 5.0);
        recommender.addUserRating("user1", "item2", 3.0);
        recommender.addUserRating("user1", "item3", 4.0);

        recommender.addUserRating("user2", "item1", 3.0);
        recommender.addUserRating("user2", "item2", 4.0);
        recommender.addUserRating("user2", "item4", 4.5);

        recommender.addUserRating("user3", "item2", 2.0);
        recommender.addUserRating("user3", "item3", 3.5);
        recommender.addUserRating("user3", "item4", 4.0);

        // 计算物品之间的相似度
        recommender.calculateItemSimilarities();

        // 推荐物品
        List<String> recommendations = recommender.recommendItems("user1", 2);
        System.out.println("推荐物品: " + recommendations);
    }
}