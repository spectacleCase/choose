package com.choose.im.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.choose.common.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 好友表
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/23 上午11:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("choose_friend")
public class Friend extends BasePo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID，表示发起好友请求的用户
     */
    private Long userId;

    /**
     * 好友ID，表示被添加为好友的用户
     */
    private Long friendId;

    /**
     * 好友关系状态 1:已添加、0:待确认、-1:已删除
     */
    private Integer status;

    /**
     * 备注，表示对好友的备注信息（如昵称、标签等）
     */
    private String remark;

}