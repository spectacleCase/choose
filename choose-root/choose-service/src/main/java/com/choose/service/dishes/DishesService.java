package com.choose.service.dishes;

import com.baomidou.mybatisplus.extension.service.IService;
import com.choose.common.dto.CommentPageDto;
import com.choose.dishes.dto.AddDishesDto;
import com.choose.dishes.dto.AddShopDto;
import com.choose.dishes.dto.DishesDetailsDto;
import com.choose.dishes.dto.ShopDetailsDto;
import com.choose.dishes.pojos.Dishes;
import com.choose.dishes.vo.DishesDetailsVo;
import com.choose.dishes.vo.ShopDetailsVo;
import com.choose.dishes.vo.ShopListVo;
import com.choose.user.dto.DishesReviewDto;
import com.choose.user.dto.ReviewDto;
import com.choose.user.vo.ReviewVo;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 上午1:53
 */
public interface DishesService extends IService<Dishes> {

    /**
     * 评论菜品
     */
    ReviewVo review(ReviewDto reviewDto);

    /**
     * 获取对应菜品的评论
     */
    List<ReviewVo> getReview(DishesReviewDto dto);

    /**
     * 添加新的店铺
     */
    void addShop(AddShopDto dto);

    /**
     * 添加新的菜品
     */
    void addMark(AddDishesDto dto);

    /**
     * 获取全部店铺
     */
    List<ShopListVo> getShopList();

    /**
     * 首页获取推荐店铺
     *
     */
    List<ShopListVo> getRecommendShops(CommentPageDto dto);

    /**
     * 根据id店铺的详情
     */
    ShopDetailsVo getShopDetails(ShopDetailsDto dto);

    /**
     * 根据id获取菜品的详情
     */
    DishesDetailsVo getDishesDetails(DishesDetailsDto dto);
}
