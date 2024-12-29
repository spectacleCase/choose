package com.choose.recommoend.pojos;

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
 * @since 2024/6/3 下午1:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "choose_recommend")
public class Recommend extends BasePo {

    /**
     * 主键id
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
     * ai描述
     */
    private String description;

    /**
     * 推荐结果(0未进入 1进入)
     */
    private Integer isSuccess;


}
