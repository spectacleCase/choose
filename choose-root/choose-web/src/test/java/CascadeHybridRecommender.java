import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于用户，物品的协同过滤的级联混合推荐
 * 实现思路：首先使用基于用户的协同过滤生成候选集，然后使用基于物品的协同过滤对候选集进行排序。
 */
public class CascadeHybridRecommender {

    // 存储用户评分的Map
    private Map<String, Map<String, Double>> userRatings;
    // 存储物品相似度的Map
    private Map<String, Map<String, Double>> itemSimilarities;

    public CascadeHybridRecommender() {
        userRatings = new HashMap<>();
        itemSimilarities = new HashMap<>();
    }

    /**
     * 添加用户对物品的评分
     * @param userId 用户ID
     * @param itemId 物品ID
     * @param rating 评分
     */
    public void addUserRating(String userId, String itemId, double rating) {
        userRatings.computeIfAbsent(userId, k -> new HashMap<>()).put(itemId, rating);
    }

    /**
     * 计算两个用户之间的余弦相似度
     * @param user1Ratings 用户1的评分
     * @param user2Ratings 用户2的评分
     * @return 余弦相似度
     */
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

    /**
     * 找到与目标用户最相似的K个用户
     * @param targetUserId 目标用户ID
     * @param k 相似用户的数量
     * @return 相似用户的ID列表
     */
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

    /**
     * 计算两个物品之间的余弦相似度
     * @param item1Ratings 物品1的评分
     * @param item2Ratings 物品2的评分
     * @return 余弦相似度
     */
    private double cosineSimilarityItems(Map<String, Double> item1Ratings, Map<String, Double> item2Ratings) {
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

    /**
     * 计算所有物品之间的相似度
     */
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
                    double similarity = cosineSimilarityItems(itemRatings.get(itemId1), itemRatings.get(itemId2));
                    itemSimilarities.computeIfAbsent(itemId1, k -> new HashMap<>()).put(itemId2, similarity);
                }
            }
        }
    }

    /**
     * 基于用户的协同过滤生成候选集
     * @param targetUserId 目标用户ID
     * @param k 相似用户的数量
     * @return 候选物品ID列表
     */
    private List<String> generateCandidateItems(String targetUserId, int k) {
        List<String> similarUsers = findSimilarUsers(targetUserId, k);
        Map<String, Double> targetUserRatings = this.userRatings.get(targetUserId);
        Set<String> candidateItems = new HashSet<>();

        for (String userId : similarUsers) {
            Map<String, Double> userRatings = this.userRatings.get(userId);
            for (Map.Entry<String, Double> entry : userRatings.entrySet()) {
                String itemId = entry.getKey();
                if (!targetUserRatings.containsKey(itemId)) {
                    candidateItems.add(itemId);
                }
            }
        }

        return new ArrayList<>(candidateItems);
    }

    /**
     * 基于物品的协同过滤对候选集进行排序
     * @param targetUserId 目标用户ID
     * @param candidateItems 候选物品ID列表
     * @return 排序后的物品ID列表
     */
    private List<String> rankCandidateItems(String targetUserId, List<String> candidateItems) {
        Map<String, Double> targetUserRatings = userRatings.get(targetUserId);
        Map<String, Double> itemScores = new HashMap<>();

        for (String itemId : candidateItems) {
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

        return itemScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 推荐物品
     * @param targetUserId 目标用户ID
     * @param k 推荐物品的数量
     * @return 推荐物品ID列表
     */
    public List<String> recommendItems(String targetUserId, int k) {
        List<String> candidateItems = generateCandidateItems(targetUserId, k);
        return rankCandidateItems(targetUserId, candidateItems);
    }

    public static void main(String[] args) {
        CascadeHybridRecommender recommender = new CascadeHybridRecommender();

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