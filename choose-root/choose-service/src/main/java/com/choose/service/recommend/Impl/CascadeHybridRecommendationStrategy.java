package com.choose.service.recommend.Impl;

import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.recommend.RecommendationStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 基于内容+用户协同过滤的级联混合推荐策略
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/14 下午10:17
 */
@Service
public class CascadeHybridRecommendationStrategy extends RecommendationStrategy {


    @Override
    public List<RecommendVo> recommendItems(String targetUserId, int k) {
        return super.recommendItems(targetUserId, k);
    }
}
