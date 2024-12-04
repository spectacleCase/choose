package com.choose.service.recommend;

import com.choose.recommoend.vo.RecommendVo;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/14 下午10:28
 */
@Data
public class RecommenderSystem {

    private RecommendationStrategy strategy;

    public List<RecommendVo> recommendItems(String targetUserId, int k) {
        return strategy.recommendItems(targetUserId, k);
    }
}
