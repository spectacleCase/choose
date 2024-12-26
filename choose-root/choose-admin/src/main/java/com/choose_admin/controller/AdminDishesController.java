package com.choose_admin.controller;

import com.choose.admin.dishes.UpdateShopStatusDto;
import com.choose.common.dto.CommentPageDto;
import com.choose.utils.Result;
import com.choose_admin.service.common.AdminCommonService;
import com.choose_admin.service.dishes.AdminDishesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/17 00:17
 */
@RestController
@RequestMapping("/choose-admin/dishes")
public class AdminDishesController {


    @Resource
    private AdminDishesService adminDishesService;

    /**
     * 获取未审核的店铺
     */
    @PostMapping("/v1/getNotExamineShop")
    public Result getNotExamineShop(@RequestBody CommentPageDto commentPageDto) {
        return Result.ok(adminDishesService.getNotExamineShop(commentPageDto));
    }

    @PostMapping("/v1/updateStatus")
    public Result updateShopStatus(@RequestBody UpdateShopStatusDto dto) {
        boolean result = adminDishesService.updateShopStatus(dto);
        return Result.ok(result);
    }
}
