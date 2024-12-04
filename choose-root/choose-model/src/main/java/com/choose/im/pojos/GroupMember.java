package com.choose.im.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 群组成员表
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/23 上午11:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("choose_group_member")
public class GroupMember extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 群组ID
     */
    private Long groupId;

    /**
     * 用户ID，表示群组成员
     */
    private Long userId;

    /**
     * 角色(1:群主、2:管理员、0:普通成员）
     */
    private Integer role;
}