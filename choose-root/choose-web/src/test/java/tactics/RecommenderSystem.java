package tactics;

import lombok.Setter;

import java.util.List;

@Setter
public class RecommenderSystem {

    private RecommendationStrategy strategy;

    public RecommenderSystem(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public List<String> recommendItems(String targetUserId, int k) {
        return strategy.recommendItems(targetUserId, k);
    }

}