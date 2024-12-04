package com.choose.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.comment.pojos.ShopComment;
import com.choose.comment.vo.AllShopCommentVo;
import com.choose.comment.vo.LatestCommentListVo;
import com.choose.comment.vo.ShopCommentDetailVo;
import com.choose.comment.vo.ShopCommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/24 下午5:58
 */
@Mapper
public interface ShopCommentMapper extends BaseMapper<ShopComment> {

    List<ShopCommentVo> selectShopCommentList(@Param("topId") Long topId,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);

    List<ShopCommentVo> selectChildrenComments(@Param("id") Long id);

    List<AllShopCommentVo> selectAllComments(@Param("topId") Long topId,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);

    ShopCommentDetailVo selectCommentDetailById(@Param("parentId") Long parentId);

    List<ShopCommentDetailVo> selectSubComments(@Param("id") Long id);

    List<LatestCommentListVo> getLatestCommentList(@Param("offset") int offset,
                                                   @Param("limit") int limit,
                                                   @Param("sortByNums") boolean sortByNums);
}
