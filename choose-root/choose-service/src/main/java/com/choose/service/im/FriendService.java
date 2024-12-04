package com.choose.service.im;

import com.choose.im.dto.FriendDto;
import com.choose.im.pojos.Friend;
import com.choose.im.vo.FriendVo;

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
     * 查询添加好友列表
     */
    List<FriendVo> getFriendList();

    // updateFriend();
} 