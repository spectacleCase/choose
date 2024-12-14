package com.choose.service.im.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.choose.im.vo.*;
import com.choose.mapper.ChatMapper;
import com.choose.mapper.FriendMapper;
import com.choose.mapper.UserMapper;
import com.choose.service.im.FriendService;
import com.choose.string.StringUtils;
import com.choose.user.pojos.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;
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

    @Resource
    private ChatMapper chatMapper;


    @Override
    public String addFriend(FriendDto friendDto) {
        Friend friend = new Friend();
        friend.setUserId(Long.valueOf(UserLocalThread.getUser().getId()));
        friend.setFriendId(Long.valueOf(friendDto.friendId()));
        friend.setRemark(friendDto.remark());
        friend.setStatus(CommonConstants.ChatFiend.ToBeConfirmed);
        LambdaQueryWrapper<Friend> friendQueryWrapper = new LambdaQueryWrapper<>();
        friendQueryWrapper.eq(Friend::getUserId, UserLocalThread.getUser().getId())
                .eq(Friend::getFriendId, friendDto.friendId())
                .or()
                .eq(Friend::getFriendId, UserLocalThread.getUser().getId())
                .eq(Friend::getUserId, friendDto.friendId());
        friendQueryWrapper.in(Friend::getStatus, CommonConstants.ChatFiend.agree, CommonConstants.ChatFiend.ToBeConfirmed);
        List<Friend> friendList = friendMapper.selectList(friendQueryWrapper);
        if (!friendList.isEmpty()) {
            return "好友已添加，请勿重复添加";
            // throw new CustomException(AppHttpCodeEnum.DATA_EXIST);
        }
        friendMapper.insert(friend);
        ns.sendComment(Long.valueOf(friendDto.friendId()), "您有一条好友请求：（备注）" + friendDto.remark());
        return "已请求好友添加";
    }

    /**
     * 查询添加好友列表
     */
    @Override
    public ArrayList<FriendListVo> getNewFriendList() {
        HashMap<String, List<FriendVo>> stringListHashMap = new HashMap<>();
        stringListHashMap.put("近三天", new ArrayList<>());
        stringListHashMap.put("三天前", new ArrayList<>());
        stringListHashMap.put("一周前", new ArrayList<>());

        LambdaQueryWrapper<Friend> friendQueryWrapper = new LambdaQueryWrapper<>();
        friendQueryWrapper.eq(Friend::getFriendId, UserLocalThread.getUser().getId());
        List<Friend> friendList = friendMapper.selectList(friendQueryWrapper);
        List<Long> ids = friendList.stream().map(Friend::getUserId).toList();
        List<User> users = userMapper.selectBatchIds(ids);
        Map<Long, User> collect = users.stream().collect(Collectors.toMap(User::getId, user -> user));
        for (Friend friend : friendList) {
            FriendVo friendVo = new FriendVo();
            friendVo.setId(String.valueOf(friend.getId()));
            friendVo.setFriendId(String.valueOf(friend.getFriendId()));
            friendVo.setRemark(friend.getRemark());
            friendVo.setStatus(friend.getStatus());
            friendVo.setFriendName(collect.get(friend.getUserId()).getNickname());
            friendVo.setFriendImage(FileConstant.COS_HOST + collect.get(friend.getUserId()).getAvatar());
            friendVo.setCreateTime(friend.getCreateTime());
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - friendVo.getCreateTime().getTime();
            long threeDays = 3 * 24 * 60 * 60 * 1000;
            long oneWeek = 7 * 24 * 60 * 60 * 1000;
            if (timeDifference <= threeDays) {
                // 近三天
                stringListHashMap.computeIfAbsent("近三天", k -> new ArrayList<>()).add(friendVo);

            } else if (timeDifference <= oneWeek) {
                // 三天前
                stringListHashMap.computeIfAbsent("三天前", k -> new ArrayList<>()).add(friendVo);

            } else {
                // 一周前
                stringListHashMap.computeIfAbsent("一周前", k -> new ArrayList<>()).add(friendVo);
            }
        }
        ArrayList<FriendListVo> friendListVos = new ArrayList<>();
        stringListHashMap.keySet().forEach(key -> {
            List<FriendVo> friendVoList = stringListHashMap.get(key);
            FriendListVo friendListVo = new FriendListVo();
            friendListVo.setFriends(friendVoList);
            friendListVo.setTitle(key);
            friendListVos.add(friendListVo);
        });
        return friendListVos;
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
        readMessage(dto);

        GetChatVo getChatVo = new GetChatVo();
        LambdaQueryWrapper<ChatMessage> wrapper = Wrappers.lambdaQuery();

        // 设置查询条件：发送方或接收方为当前用户或目标用户
        wrapper.and(w -> w.eq(ChatMessage::getSender, dto.getId())
                .eq(ChatMessage::getReceiver, UserLocalThread.getUser().getId())
                .or()
                .eq(ChatMessage::getSender, UserLocalThread.getUser().getId())
                .eq(ChatMessage::getReceiver, dto.getId()));

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
        ArrayList<ChatMessageVo> chatMessageVos = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessages) {
            ChatMessageVo chatMessageVo = new ChatMessageVo();
            BeanUtils.copyProperties(chatMessage, chatMessageVo);
            chatMessageVo.setId(String.valueOf(chatMessage.getId()));
            chatMessageVo.setSender(String.valueOf(chatMessage.getSender()));
            chatMessageVo.setReceiver(String.valueOf(chatMessage.getReceiver()));
            chatMessageVos.add(chatMessageVo);
        }

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
        getChatVo.setChatList(chatMessageVos);
        return getChatVo;
    }

    @Override
    public void readMessage(getChatListDto dto) {
        ChatMessage chatMessage1 = new ChatMessage();
        chatMessage1.setIsRead(1);
        chatMapper.update(chatMessage1, new LambdaQueryWrapper<ChatMessage>().eq(ChatMessage::getSender, dto.getId())
                .eq(ChatMessage::getReceiver, UserLocalThread.getUser().getId()));
    }

    /**
     * 获取全部好友
     */
    @Override
    public List<GetFriendListVo> getFriendList() {
        HashMap<String, List<GetFVo>> stringListHashMap = new HashMap<>();
        ArrayList<GetFriendListVo> getFriendListVos = new ArrayList<>();
        LambdaQueryWrapper<Friend> fw = new LambdaQueryWrapper<>();
        fw.and(w -> w.eq(Friend::getFriendId, UserLocalThread.getUser().getId())
                .or()
                .eq(Friend::getUserId, UserLocalThread.getUser().getId())
        ).eq(Friend::getStatus, 1);
        List<Friend> r = friendMapper.selectList(fw);
        List<Long> ids = new ArrayList<>();
        r.forEach(f -> {
            if (String.valueOf(f.getFriendId()).equals(UserLocalThread.getUser().getId())) {
                ids.add(f.getUserId());
            } else {
                ids.add(f.getFriendId());
            }
        });
        if (ids.isEmpty()) {
            return List.of();
        }
        List<User> users = userMapper.selectBatchIds(ids);
        users.forEach(u -> {
            String s = StringUtils.capitalizeFirstLetterOfPinyin(u.getNickname());
            if (!stringListHashMap.containsKey(s)) {
                stringListHashMap.putIfAbsent(s, new ArrayList<>());
            }
            GetFVo get = new GetFVo();
            get.setAvatar(FileConstant.COS_HOST + u.getAvatar());
            get.setUsername(u.getNickname());
            get.setId(String.valueOf(u.getId()));
            stringListHashMap.get(s).add(get);
        });
        stringListHashMap.keySet().forEach(s -> {
            GetFriendListVo getFriendListVo = new GetFriendListVo();
            getFriendListVo.setLetter(s);
            getFriendListVo.setContacts(stringListHashMap.get(s));
            getFriendListVos.add(getFriendListVo);
        });
        return getFriendListVos;
    }

    /**
     * 获取在线聊天的用户
     */
    @Override
    public List<GetFVo> getChatUserList() {
        LambdaQueryWrapper<Friend> fw = new LambdaQueryWrapper<>();
        fw.and(w -> w.eq(Friend::getFriendId, UserLocalThread.getUser().getId())
                .or()
                .eq(Friend::getUserId, UserLocalThread.getUser().getId())
        ).eq(Friend::getStatus, 1);
        List<Friend> r = friendMapper.selectList(fw);
        List<Long> ids = new ArrayList<>();
        r.forEach(f -> {
            if (String.valueOf(f.getFriendId()).equals(UserLocalThread.getUser().getId())) {
                ids.add(f.getUserId());
            } else {
                ids.add(f.getFriendId());
            }
        });
        if (ids.isEmpty()) {
            return List.of();
        }
        List<User> users = userMapper.selectBatchIds(ids);
        LambdaQueryWrapper<ChatMessage> notReadCountListQ = new LambdaQueryWrapper<>();
        notReadCountListQ.eq(ChatMessage::getReceiver, UserLocalThread.getUser().getId())
                .eq(ChatMessage::getIsRead, 0);

        List<ChatMessage> notReadCountList = chatMapper.selectList(notReadCountListQ);

        Map<Long, Long> notReadCountMap = notReadCountList.stream()
                .collect(Collectors.groupingBy(ChatMessage::getSender, Collectors.counting()));
        ArrayList<GetFVo> getChatVos = new ArrayList<>();
        List<ChatMessage> chatMessages = chatMapper.getChatUserList(UserLocalThread.getUser().getId());

        // 按用户 ID 分组聊天记录
        Map<Long, List<ChatMessage>> collect = chatMessages.stream()
                .collect(Collectors.groupingBy(ChatMessage::getReceiver));
        Map<Long, List<ChatMessage>> collect1 = chatMessages.stream().collect(Collectors.groupingBy(ChatMessage::getSender));


        users.forEach(u -> {
            GetFVo getFVo = new GetFVo();
            getFVo.setId(String.valueOf(u.getId()));
            getFVo.setUsername(u.getNickname());
            getFVo.setAvatar(FileConstant.COS_HOST + u.getAvatar());
            getFVo.setNotReadCount(notReadCountMap.get(u.getId()) != null ? notReadCountMap.get(u.getId()) : 0L);

            // 获取对应用户的最新一条聊天记录
            List<ChatMessage> userChatMessages = collect.getOrDefault(u.getId(), Collections.emptyList());
            List<ChatMessage> userChatMessages1 = collect1.getOrDefault(u.getId(), Collections.emptyList());
            if (!userChatMessages.isEmpty() && !userChatMessages1.isEmpty()) {
                ChatMessage chatMessage = userChatMessages.get(0);
                ChatMessage chatMessage1 = userChatMessages1.get(0);
                if (chatMessage1.getCreateTime().getTime() > chatMessage.getCreateTime().getTime()) {
                    getFVo.setCreateTime(formatCreateTime(String.valueOf(chatMessage1.getCreateTime())));
                    getFVo.setChat(chatMessage1.getContent());
                } else {
                    getFVo.setCreateTime(formatCreateTime(String.valueOf(chatMessage.getCreateTime())));
                    getFVo.setChat(chatMessage.getContent());
                }
            } else if (!userChatMessages.isEmpty()) {
                ChatMessage latestMessage = userChatMessages.get(0);
                getFVo.setCreateTime(formatCreateTime(String.valueOf(latestMessage.getCreateTime())));
                getFVo.setChat(latestMessage.getContent());
            } else if (!userChatMessages1.isEmpty()) {
                ChatMessage latestMessage = userChatMessages1.get(0);
                getFVo.setCreateTime(formatCreateTime(String.valueOf(latestMessage.getCreateTime())));
                getFVo.setChat(latestMessage.getContent());
            } else {
                // 如果没有聊天记录，可以设置默认值或不设置

                getFVo.setCreateTime("");
                getFVo.setChat("");
            }

            getChatVos.add(getFVo);
        });
        return getChatVos;


    }

    public static String formatCreateTime(String createTimeStr) {
        // 解析时间字符串
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        ZonedDateTime createTime = ZonedDateTime.parse(createTimeStr, inputFormatter);

        // 获取当前时间
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        // 计算时间差
        long days = java.time.temporal.ChronoUnit.DAYS.between(createTime.toLocalDate(), now.toLocalDate());

        // 格式化时间
        if (days == 0) {
            // 今天
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
            return createTime.format(outputFormatter);
        } else if (days == 1) {
            // 昨天
            return "昨天";
        } else if (days > 1 && days <= 7) {
            // 一周内
            return createTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        } else {
            // 一周外
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return createTime.format(outputFormatter);
        }
    }
}
