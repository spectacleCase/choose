package com.choose.service.comment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.choose.comment.dto.AddShopCommentDto;
import com.choose.comment.dto.ShopCommentDetailDto;
import com.choose.comment.dto.ShopCommentDto;
import com.choose.comment.pojos.ShopComment;
import com.choose.comment.vo.*;
import com.choose.common.CommentPageDto;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/24 下午6:00
 */
public interface ShopCommentService extends IService<ShopComment> {

    /**
     * 添加店铺评论
     */
    void addShopComment(AddShopCommentDto dto);

    /**
     * 获取发送给用户的消息
     */
    List<UserCommentVo> getUserComment(CommentPageDto dto);

    /**
     * 获取店铺首页的全部评论
     */
    List<ShopCommentVo> getShopCommentList(ShopCommentDto dto);

    /**
     * 获取店铺的全部评论
     */
    List<AllShopCommentVo> getShopAllCommentList(ShopCommentDto dto);

    /**
     * 获取评论的详情
     */
    ShopCommentDetailVo getShpCommentDetails(ShopCommentDetailDto dto);

    /**
     * 获取最新的评论列表
     */
    List<LatestCommentListVo> getLatestCommentList(CommentPageDto dto,Boolean sortByNums);
}
