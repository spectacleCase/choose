package com.choose.controller.im;

import com.choose.im.dto.FriendDto;
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

    @PostMapping("/v1/addFriend")
    public Result addFriend(@RequestBody FriendDto friendDto) {
        friendService.addFriend(friendDto);
        return Result.ok();
    }

    @PostMapping("/v1/getFriendList")
    public Result getFriendList() {
        return Result.ok(friendService.getFriendList());
    }

    // @PostMapping("/v1/updateFriend")
    // public Result updateFriend(@RequestBody FriendDto friendDto) {
    //     // friendService
    // }
}
