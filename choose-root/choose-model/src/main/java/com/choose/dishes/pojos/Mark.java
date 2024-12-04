package com.choose.dishes.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 上午1:24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_mark")
public class Mark extends BasePo {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 菜品id
     */
    private Long dishesId;

    /**
     * 分数
     */
    private Integer mark;

}
