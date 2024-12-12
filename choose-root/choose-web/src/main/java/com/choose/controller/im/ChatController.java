package com.choose.controller.im;

import com.choose.im.dto.FriendDto;
import com.choose.im.dto.FriendStatusDto;
import com.choose.im.dto.SelectFriendDto;
import com.choose.im.dto.getChatListDto;
import com.choose.service.im.FriendService;
import com.choose.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/3 上午10:19
 */
@RestController
@RequestMapping("/choose/chat")
public class ChatController {

    @Resource
    private FriendService friendService;

    // todo 只有 -1 0 的同意状态
    @PostMapping("/v1/addFriend")
    public Result addFriend(@RequestBody FriendDto friendDto) {
        friendService.addFriend(friendDto);
        return Result.ok();
    }

    @PostMapping("/v1/selectFriend")
    public Result selectFriend(@RequestBody SelectFriendDto selectFriendDto) {
        return Result.ok(friendService.selectFriend(selectFriendDto));
    }

    @PostMapping("/v1/getNewFriendList")
    public Result getNewFriendList() {
        return Result.ok(friendService.getNewFriendList());
    }

    @PostMapping("/v1/getFriendList")
    public Result getFriendList() {
        return Result.ok(friendService.getFriendList());
    }

    @PostMapping("/v1/updateFriend")
    public Result updateFriend(@RequestBody FriendStatusDto friendDto) {
        friendService.updateFriend(friendDto);
        return Result.ok();
    }

    @PostMapping("/v1/getChatList")
    public Result getChatList(@RequestBody getChatListDto dto) {
        return Result.ok(friendService.getChatList(dto));
    }

    @PostMapping("/v1/readMessage")
    public Result readMessage(@RequestBody getChatListDto dto) {
        friendService.readMessage(dto);
        return Result.ok();
    }

    @PostMapping("/v1/getChatUserList")
    public Result getChatUserList() {
        return Result.ok(friendService.getChatUserList());
    }


}
