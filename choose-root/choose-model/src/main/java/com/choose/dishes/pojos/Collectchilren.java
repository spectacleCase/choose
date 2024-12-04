package com.choose.dishes.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chen
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_collect_chilren")
public class Collectchilren extends BasePo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private Long userid;
    private Long dishId;
    private String dishesName;
    private String dishesImage;
    private String coordinate;
}

