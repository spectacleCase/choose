package com.choose.agent.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜品营养成分。论文6.1 表6-2 TC-12 / 知识库管理模块的核心实体。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_dish_nutrition")
public class DishNutrition extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 菜品ID,与 choose_dishes.id 关联 */
    private Long dishId;

    /** 热量 (千卡) */
    private Double calorie;

    /** 蛋白质 (g) */
    private Double protein;

    /** 脂肪 (g) */
    private Double fat;

    /** 碳水化合物 (g) */
    private Double carbs;

    /** 膳食纤维 (g) */
    private Double fiber;

    /** 钠 (mg) */
    private Double sodium;

    /** 健康标签数组 JSON eg. ["低脂","高蛋白"] */
    private String healthTags;

    /** 数据来源: manual / llm_estimate */
    private String source;
}
