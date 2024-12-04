package com.choose.im.vo;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/3 上午11:13
 */
@Data
public class FriendVo {

    private String id;


    /**
     * 好友ID，表示被添加为好友的用户
     */
    private String friendId;

    private String friendName;

    private String friendImage;

    /**
     * 好友关系状态 1:已添加、0:待确认、-1:已删除
     */
    private Integer status;

    /**
     * 备注，表示对好友的备注信息（如昵称、标签等）
     */
    private String remark;

    private Date createTime;


}
