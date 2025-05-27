package com.choose.dishes.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 食物热量信息表实体类
 * @author lizhentao
 */
@Data
@TableName(value = "choose_foods_heat")
public class FoodsHeat {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 食物的名称
     */
    private String name;

    /**
     * 食物的别称
     */
    private String alias;

    /**
     * 食物的热量，单位为大卡
     */
    private Integer calories;

    /**
     * 食物的比例信息，例如100(单位克)
     */
    private Integer proportion;
}