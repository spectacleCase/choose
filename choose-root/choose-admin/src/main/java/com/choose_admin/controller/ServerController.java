package com.choose_admin.controller;

import com.choose.admin.system.Server;
import com.choose.annotation.SysLog;
import com.choose.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 服务器监控
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/7/3 下午10:05
 */
@RestController
@RequestMapping("/choose-admin/server")
public class ServerController {

    @PostMapping("/v1/get")
    @SysLog("服务器性能监控")
    public Result getSystemInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return Result.ok(server);
    }

}

