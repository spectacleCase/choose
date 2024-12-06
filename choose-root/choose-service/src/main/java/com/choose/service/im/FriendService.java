package com.choose.service.im;

import com.choose.im.dto.FriendDto;
import com.choose.im.dto.FriendStatusDto;
import com.choose.im.dto.SelectFriendDto;
import com.choose.im.dto.getChatListDto;
import com.choose.im.vo.FriendVo;
import com.choose.im.vo.SelectFriendVo;
import com.choose.im.vo.GetChatVo;

import java.util.List;

/**
 * @author 桌角的眼镜
 */
public interface FriendService {
    /**
     * 好友请求
     */
    void addFriend(FriendDto friendDto);

    /**
     * 获取添加好友列表
     */
    List<FriendVo> getFriendList();

    /**
     * 修改好友状态
     */
    void updateFriend(FriendStatusDto friendDto);

    /**
     * 查询好友列表
     */
    List<SelectFriendVo> selectFriend(SelectFriendDto selectFriendDto);

    /**
     * 获取俩天记录
     */
    GetChatVo getChatList(getChatListDto dto);
} 