package com.choose.controller.dishes;

import com.choose.common.dto.CommentPageDto;
import com.choose.dishes.dto.*;
import com.choose.service.dishes.DishesService;
import com.choose.service.ranking.MarkService;
import com.choose.user.dto.DishesReviewDto;
import com.choose.user.dto.ReviewDto;
import com.choose.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 菜品控制器
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 下午4:51
 */
@RestController
@RequestMapping("/choose/dishes")
public class DishesController {


    @Resource
    private MarkService markService;

    @Resource
    private DishesService dishesService;

    @PostMapping("/v1/dishes-mark")
    public Result dishesMark(@Valid @RequestBody MarkDto dto) {
        return Result.ok(markService.dishesMark(dto));
    }

    @PostMapping("/v1/review")
    public Result review(@Valid @RequestBody ReviewDto dto) {
        return Result.ok(dishesService.review(dto));
    }

    @PostMapping("/v1/getReview")
    public Result getReview(@Valid @RequestBody DishesReviewDto dto) {
        return Result.ok(dishesService.getReview(dto));
    }

    @PostMapping("/v1/addShop")
    public Result addShop(@Valid @RequestBody AddShopDto dto) {
        dishesService.addShop(dto);
        return Result.ok();
    }

    @PostMapping("/v1/addMark")
    public Result addMark(@Valid @RequestBody AddDishesDto dto) {
        dishesService.addMark(dto);
        return Result.ok();
    }

    @PostMapping("/v1/getShopList")
    public Result getShopList() {
        return Result.ok(dishesService.getShopList());
    }

    @PostMapping("/v1/getRecommendShops")
    public Result getRecommendShops(@Valid @RequestBody CommentPageDto dto) {
        return Result.ok(dishesService.getRecommendShops(dto));
    }

    @PostMapping("/v1/getShopDetails")
    public Result getShopDetails(@Valid @RequestBody ShopDetailsDto dto) {
        return Result.ok(dishesService.getShopDetails(dto));
    }

    @PostMapping("/v1/getDishesDetails")
    public Result getDishesDetails(@Valid @RequestBody DishesDetailsDto dto) {
        return Result.ok(dishesService.getDishesDetails(dto));
    }

}
