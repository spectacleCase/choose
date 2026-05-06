package com.choose.agent.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜品-食材关联表 (论文 5.2 食材成分管理)。
 * 一条记录代表某菜品里包含某食材若干克。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_dish_ingredient")
public class DishIngredient extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 菜品ID */
    private Long dishId;

    /** 食材ID */
    private Long ingredientId;

    /** 一份菜里该食材的分量 (g) */
    private Double amount;
}
