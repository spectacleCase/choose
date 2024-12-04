package com.choose.im.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 群组表
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/23 上午11:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("choose_group")
public class Group extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 群组名称
     */
    private String name;

    /**
     * 群主ID
     */
    private Long ownerId;

    /**
     * 群组头像
     */
    private String avatar;
}