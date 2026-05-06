package com.choose.agent.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 食材主数据 (论文 5.2 食材成分管理)。
 * 字段记录每 100g 该食材的营养指标。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_ingredient")
public class Ingredient extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 食材名称 */
    private String name;

    /** 类别: 肉类 / 蔬菜 / 主食 / 调味 等 */
    private String category;

    /** 每 100g 热量 kcal */
    private Double calorie;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double fiber;
    private Double sodium;
}
