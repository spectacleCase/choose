package com.choose.service.recommend;

import com.choose.common.dto.CommentPageDto;
import com.choose.recommoend.dto.RecommendDto;
import com.choose.recommoend.vo.RecommendListVo;
import com.choose.recommoend.vo.RecommendVo;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/3 下午1:47
 */
public interface RecommendService {

    /**
     * 推荐记录
     */
    List<RecommendListVo> recommendRecord(CommentPageDto dto);

    /**
     * 生产版推荐菜品
     */
    public List<RecommendVo> recommendPlus(int num);

    /**
     * 生产版推荐菜品
     */
    public List<RecommendVo> recommendMinus(RecommendDto dto);

}
