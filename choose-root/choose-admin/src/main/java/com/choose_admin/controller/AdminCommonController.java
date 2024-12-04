package com.choose_admin.controller;

import com.choose.utils.Result;
import com.choose_admin.service.common.AdminCommonService;
import org.springframework.web.bind.annotation.PostMapping;
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
 * @since 2024/11/11 下午3:37
 */
@RestController
@RequestMapping("/choose-admin/common")
public class AdminCommonController {

    @Resource
    private AdminCommonService adminCommonService;

    /**
     * 获取首页的内容
     */
    @PostMapping("/v1/getIndex")
    public Result getIndex() {
        return Result.ok(adminCommonService.getIndex());
    }
}
