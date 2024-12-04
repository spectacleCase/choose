package com.choose_admin.controller;

import com.choose.admin.user.dto.User;
import com.choose.utils.Result;
import com.choose_admin.service.auto.AutoService;
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
 * @since 2024/6/21 下午10:02
 */
@RestController
@RequestMapping("/choose-admin/auto")
public class AutoController {

    @Resource
    private AutoService autoService;

    @PostMapping("/v1/login")
    public Result login(@RequestBody User user) {
        return Result.ok(autoService.login(user));
    }

    // 退出登录
    @PostMapping("/v1/logout")
    public Result logout() {
        autoService.logout();
        return Result.ok();
    }


}
