package com.choose.controller.user;


import com.choose.constant.CommonConstants;
import com.choose.service.auto.UserService;
import com.choose.service.common.CommonService;
import com.choose.tag.dto.TagAssociationDto;
import com.choose.user.dto.LoginDto;
import com.choose.user.pojos.UserInfo;
import com.choose.user.vo.UserInfoVo;
import com.choose.user.vo.UserVo;
import com.choose.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

/**
 * <p>
 * 用户中心控制器
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 上午12:59
 */

@RestController
@Slf4j
@RequestMapping("/choose/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private CommonService commonService;

    @PostMapping("/v1/login-applet")
    public Result login(@Valid @RequestBody LoginDto dto) {
        log.info("login-applet:code:{}", dto.getCode());
        UserInfoVo userInfoVo = userService.login(dto);
        if (Objects.isNull(userInfoVo)) {
            return Result.error(CommonConstants.ResultCode.ERROR.code, CommonConstants.ResultCode.ERROR.message);
        }
        return Result.ok(userInfoVo);
    }


    @PostMapping("/v1/updateAvatar")
    public Result updateAvatar(@RequestBody String fileName) {
        userService.updateAvatar(fileName);
        return Result.ok();
    }


    @PostMapping("/v1/token-expired")
    public Result refresh(HttpServletRequest request) {
        String token = request.getHeader("Token");
        String newToken = userService.refresh(token);
        return Result.ok(newToken);
    }

    @PostMapping("/v1/getUser")
    public Result getUser() {
        UserInfo user = userService.getUser();
        return Result.ok(user);
    }

    @PostMapping("/v1/setUser")
    public Result setUser(@RequestBody UserVo userVo) {
        UserInfo user = userService.setUser(userVo);
        return Result.ok(user);
    }

    @PostMapping("/v1/health-tips")
    public Result healthTips() {
        return Result.ok(commonService.healthTips());
    }

    @PostMapping("/v1/appendTag")
    public Result appendTag(@RequestBody TagAssociationDto vo) {
        userService.addendTag(vo);
        return Result.ok();
    }

    @PostMapping("/v1/getUserTag")
    public Result getUserTag() {
        return Result.ok(userService.getUserTag());
    }

    @PostMapping("/v1/getUserOR")
    public Result getUserOR() {
        return Result.ok(userService.userORCode());
    }


}
