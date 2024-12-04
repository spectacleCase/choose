package tactics;

import java.util.List;

public interface RecommendationStrategy {
    List<String> recommendItems(String targetUserId, int k);
}