package com.choose.controller.comment;

import com.choose.comment.dto.AddShopCommentDto;
import com.choose.comment.dto.ShopCommentDetailDto;
import com.choose.comment.dto.ShopCommentDto;
import com.choose.common.dto.CommentPageDto;
import com.choose.service.comment.CommentNotificationsService;
import com.choose.service.comment.ShopCommentService;
import com.choose.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/10/24 下午6:01
 */
@RestController
@Slf4j
@RequestMapping("/choose/comment")
public class CommentController {


    @Resource
    private ShopCommentService shopCommentService;

    @Resource
    private CommentNotificationsService commentNotificationsService;


    @PostMapping("/v1/addShopComment")
    private Result addShopComment(@Valid @RequestBody AddShopCommentDto dto) {
        shopCommentService.addShopComment(dto);
        return Result.ok();
    }

    @PostMapping("/v1/getLatestCommentList")
    private Result getLatestCommentList(@Valid @RequestBody CommentPageDto dto) {
        return Result.ok(shopCommentService.getLatestCommentList(dto, false));
    }

    @PostMapping("/v1/getHotCommentList")
    private Result getHotCommentList(@Valid @RequestBody CommentPageDto dto) {
        return Result.ok(shopCommentService.getLatestCommentList(dto, true));
    }

    @PostMapping("/v1/getIsReadNum")
    private Result getIsReadNum() {
        return Result.ok(commentNotificationsService.getIsReadNum());
    }

    @PostMapping("/v1/read")
    private Result read() {
        commentNotificationsService.read();
        return Result.ok();
    }

    @PostMapping("/v1/getUserComment")
    private Result getUserComment(@Valid @RequestBody CommentPageDto dto) {
        return Result.ok(shopCommentService.getUserComment(dto));
    }

    @PostMapping("/v1/getShopCommentList")
    private Result getShopCommentList(@Valid @RequestBody ShopCommentDto dto) {
        return Result.ok(shopCommentService.getShopCommentList(dto));
    }

    @PostMapping("/v1/getShopAllCommentList")
    private Result getShopAllCommentList(@Valid @RequestBody ShopCommentDto dto) {
        return Result.ok(shopCommentService.getShopAllCommentList(dto));
    }

    @PostMapping("/v1/getShpCommentDetails")
    private Result getShpCommentDetails(@Valid @RequestBody ShopCommentDetailDto dto) {

        return Result.ok(shopCommentService.getShpCommentDetails(dto));
    }

}
