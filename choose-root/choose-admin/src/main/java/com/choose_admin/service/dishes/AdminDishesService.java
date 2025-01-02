package com.choose_admin.service.dishes;

import com.choose.admin.dishes.UpdateStatusDto;
import com.choose.common.dto.CommentPageDto;
import com.choose.dishes.vo.AdminDishesVo;
import com.choose.dishes.vo.AdminShopVo;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/17 00:21
 */
public interface AdminDishesService {

    public List<AdminShopVo> getNotExamineShop(CommentPageDto commentPageDto);

    // 修改店铺状态
    boolean updateShopStatus(UpdateStatusDto dto);

    List<AdminDishesVo> getNotExamineDishes(CommentPageDto commentPageDto);

    boolean updateDishesStatus(UpdateStatusDto dto);
}
