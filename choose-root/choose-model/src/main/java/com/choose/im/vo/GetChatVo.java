package com.choose.im.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/6 下午10:10
 */
@Data
public class GetChatVo {

    private List<ChatMessageVo> chatList;

    private String avatar;

    private String id;

    private String nickname;

    private String seAvatar;

    private String seId;

    private String seNickname;


}
