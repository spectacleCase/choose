package com.choose.service.im.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.choose.config.UserLocalThread;
import com.choose.constant.CommonConstants;
import com.choose.constant.FileConstant;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.im.dto.FriendDto;
import com.choose.im.dto.FriendStatusDto;
import com.choose.im.dto.SelectFriendDto;
import com.choose.im.dto.getChatListDto;
import com.choose.im.pojos.ChatMessage;
import com.choose.im.pojos.Friend;
import com.choose.im.vo.FriendVo;
import com.choose.im.vo.SelectFriendVo;
import com.choose.im.vo.GetChatVo;
import com.choose.mapper.ChatMapper;
import com.choose.mapper.FriendMapper;
import com.choose.mapper.UserMapper;
import com.choose.service.im.FriendService;
import com.choose.user.pojos.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private ChatMapper chatMapper;


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
        if (!friendList.isEmpty()) {
            throw new CustomException(AppHttpCodeEnum.DATA_EXIST);
        }
        friendMapper.insert(friend);
        ns.sendComment(Long.valueOf(friendDto.friendId()), "您有一条好友请求：（备注）" + friendDto.remark());
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

    /**
     * 修改好友状态
     */
    @Override
    public void updateFriend(FriendStatusDto friendDto) {

        Friend friend = friendMapper.selectById(friendDto.id());
        if (Objects.nonNull(friend)) {
            if (friendDto.status() >= 2) {
                throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
            }
            friend.setStatus(friendDto.status());
            friendMapper.updateById(friend);
            return;
        }

        throw new CustomException(AppHttpCodeEnum.DATA_EXIST);
    }

    /**
     * 查询好友
     */
    @Override
    public List<SelectFriendVo> selectFriend(SelectFriendDto selectFriendDto) {
        if (StringUtils.isEmpty(selectFriendDto.id()) && StringUtils.isEmpty(selectFriendDto.nickname())) {
            throw new CustomException(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        LambdaQueryWrapper<User> r = new LambdaQueryWrapper<>();
        r.ne(User::getId, UserLocalThread.getUser().getId());
        ArrayList<SelectFriendVo> selectFriendVos = new ArrayList<>();
        if (!StringUtils.isEmpty(selectFriendDto.nickname())) {
            r.like(User::getNickname, selectFriendDto.nickname());
        } else if (!StringUtils.isEmpty(selectFriendDto.id())) {
            r.eq(User::getId, selectFriendDto.id());
        }
        List<User> users = userMapper.selectList(r);
        List<Long> list = users.stream().map(User::getId).toList();
        LambdaQueryWrapper<Friend> fw = new LambdaQueryWrapper<>();
        fw.in(Friend::getFriendId, list);
        List<Friend> friendList = friendMapper.selectList(fw);
        Map<Long, Integer> collect = friendList.stream().collect(Collectors.toMap(Friend::getId, Friend::getStatus));
        if (!users.isEmpty()) {
            for (User user : users) {
                SelectFriendVo selectFriendVo = new SelectFriendVo();
                BeanUtils.copyProperties(user, selectFriendVo);
                selectFriendVo.setId(String.valueOf(user.getId()));
                selectFriendVos.add(selectFriendVo);
                selectFriendVo.setStatus(collect.get(user.getId()));
            }
        }
        return selectFriendVos;
    }

    /**
     * 获取俩天记录
     */
    @Override
    public GetChatVo getChatList(getChatListDto dto) {
        GetChatVo getChatVo = new GetChatVo();
        LambdaQueryWrapper<ChatMessage> wrapper = Wrappers.lambdaQuery();

        // 设置查询条件：发送方或接收方为当前用户或目标用户
        wrapper.and(w -> w.eq(ChatMessage::getSender, dto.getId())
                        .eq(ChatMessage::getReceiver, UserLocalThread.getUser().getId())
                        .or()
                        .eq(ChatMessage::getSender, UserLocalThread.getUser().getId())
                        .eq(ChatMessage::getReceiver, dto.getId()))
                .orderByDesc(ChatMessage::getCreateTime);

        // 如果是第一次查询，获取最近一周的聊天记录
        if (dto.getLastCreateTime() == null) {
            LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
            wrapper.ge(ChatMessage::getCreateTime, oneWeekAgo);
        } else {
            // 否则，根据上次查询的最大 create_time 来查询更早的聊天记录
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime lastCreateTime = LocalDateTime.parse(dto.getLastCreateTime(), formatter);
            wrapper.lt(ChatMessage::getCreateTime, lastCreateTime);
        }

        // 执行查询
        List<ChatMessage> chatMessages = chatMapper.selectList(wrapper);

        // 查询用户信息
        LambdaQueryWrapper<User> ulw = new LambdaQueryWrapper<>();
        ulw.in(User::getId, dto.getId(), UserLocalThread.getUser().getId());
        List<User> users = userMapper.selectList(ulw);

        for (User user : users) {
            if (String.valueOf(user.getId()).equals(dto.getId())) {
                getChatVo.setSeId(String.valueOf(user.getId()));
                getChatVo.setSeAvatar(FileConstant.COS_HOST + user.getAvatar());
                getChatVo.setSeNickname(user.getNickname());
            } else {
                getChatVo.setId(String.valueOf(user.getId()));
                getChatVo.setAvatar(FileConstant.COS_HOST + user.getAvatar());
                getChatVo.setNickname(user.getNickname());
            }
        }
        getChatVo.setChatList(chatMessages);
        return getChatVo;
    }

}
