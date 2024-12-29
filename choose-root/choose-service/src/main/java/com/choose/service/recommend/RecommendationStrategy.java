package com.choose.service.recommend;

import com.choose.mapper.*;
import com.choose.recommoend.vo.RecommendVo;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 推荐策略
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/14 下午9:58
 */
public abstract class RecommendationStrategy {

    @Resource
    protected DishesMapper dishesMapper;

    @Resource
    protected RecommendMapper recommendMapper;

    @Resource
    protected TagAssociationMapper tagAssociationMapper;

    @Resource
    protected ShopsMapper shopsMapper;

    @Resource
    protected TagMapper tagMapper;


    /**
     * 推荐
     * @param targetUserId - 目标用户id
     * @param k - 个数
     * @return - 推荐结果
     */
    public List<RecommendVo> recommendItems(String targetUserId, int k){
        return new ArrayList<>();
    };
}
