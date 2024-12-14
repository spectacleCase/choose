package com.choose.service.im;

import com.choose.im.dto.FriendDto;
import com.choose.im.dto.FriendStatusDto;
import com.choose.im.dto.SelectFriendDto;
import com.choose.im.dto.getChatListDto;
import com.choose.im.vo.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 桌角的眼镜
 */
public interface FriendService {
    /**
     * 好友请求
     */
    String addFriend(FriendDto friendDto);

    /**
     * 获取添加好友列表
     */
    ArrayList<FriendListVo> getNewFriendList();

    /**
     * 修改好友状态
     */
    void updateFriend(FriendStatusDto friendDto);

    /**
     * 查询好友列表
     */
    List<SelectFriendVo> selectFriend(SelectFriendDto selectFriendDto);

    /**
     * 获取聊天记录
     */
    GetChatVo getChatList(getChatListDto dto);

    /**
     * 已读全部信息
     */
    void readMessage(getChatListDto dto);

    /**
     * 获取全部好友
     */
    List<GetFriendListVo> getFriendList();

    /**
     * 获取在线聊天的用户
     */
    List<GetFVo> getChatUserList();
}