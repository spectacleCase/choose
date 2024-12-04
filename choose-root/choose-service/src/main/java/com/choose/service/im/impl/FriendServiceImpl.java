package com.choose.service.im.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.choose.config.UserLocalThread;
import com.choose.constant.CommonConstants;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.im.dto.FriendDto;
import com.choose.im.pojos.Friend;
import com.choose.im.vo.FriendVo;
import com.choose.mapper.FriendMapper;
import com.choose.mapper.UserMapper;
import com.choose.service.im.FriendService;
import com.choose.user.pojos.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.choose.enums.AppHttpCodeEnum.PARAM_REQUIRE;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/3 上午10:18
 */
@Service
public class FriendServiceImpl implements FriendService {

    @Resource
    private FriendMapper friendMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private NotificationWebSocketHandlerServer ns;


    @Override
    public void addFriend(FriendDto friendDto) {
        Friend friend = new Friend();
        friend.setUserId(Long.valueOf(UserLocalThread.getUser().getId()));
        friend.setFriendId(Long.valueOf(friendDto.friendId()));
        friend.setRemark(friendDto.remark());
        friend.setStatus(CommonConstants.ChatFiend.ToBeConfirmed);
        LambdaQueryWrapper<Friend> friendQueryWrapper = new LambdaQueryWrapper<>();
        friendQueryWrapper.eq(Friend::getUserId, UserLocalThread.getUser().getId());
        friendQueryWrapper.eq(Friend::getFriendId, friendDto.friendId());
        friendQueryWrapper.in(Friend::getStatus, CommonConstants.ChatFiend.agree, CommonConstants.ChatFiend.ToBeConfirmed);
        List<Friend> friendList = friendMapper.selectList(friendQueryWrapper);
        if(!friendList.isEmpty()){
            throw new CustomException(AppHttpCodeEnum.DATA_EXIST);
        }
        friendMapper.insert(friend);
        ns.sendComment(Long.valueOf(friendDto.friendId()),"您有一条好友请求：（备注）" + friendDto.remark());
    }

    /**
     * 查询添加好友列表
     */
    @Override
    public List<FriendVo> getFriendList() {
        LambdaQueryWrapper<Friend> friendQueryWrapper = new LambdaQueryWrapper<>();
        friendQueryWrapper.eq(Friend::getUserId, UserLocalThread.getUser().getId());
        List<Friend> friendList = friendMapper.selectList(friendQueryWrapper);
        List<Long> ids = friendList.stream().map(Friend::getFriendId).toList();
        List<User> users = userMapper.selectBatchIds(ids);
        Map<Long, User> collect = users.stream().collect(Collectors.toMap(User::getId, user -> user));
        ArrayList<FriendVo> friendVos = new ArrayList<>(users.size());
        for (Friend friend : friendList) {
            FriendVo friendVo = new FriendVo();
            friendVo.setId(String.valueOf(friend.getId()));
            friendVo.setFriendId(String.valueOf(friend.getFriendId()));
            friendVo.setRemark(friend.getRemark());
            friendVo.setStatus(friend.getStatus());
            friendVo.setFriendName(collect.get(friend.getFriendId()).getNickname());
            friendVo.setFriendImage(collect.get(friend.getFriendId()).getAvatar());
            friendVo.setCreateTime(friend.getCreateTime());
            friendVos.add(friendVo);
        }
        return friendVos;
    }
}
