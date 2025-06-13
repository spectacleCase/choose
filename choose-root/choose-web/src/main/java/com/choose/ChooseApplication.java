package com.choose;

import com.choose.service.common.SysLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 上午12:09
 */
@SpringBootApplication(scanBasePackages = {"com.choose", "com.choose_admin", "com.choose_ai"})
@EnableScheduling // 开启调度任务1
@EnableAspectJAutoProxy(exposeProxy = true)
public class ChooseApplication implements CommandLineRunner {

    @Resource
    private SysLogService sysLogService;

    public static void main(String[] args) {
        SpringApplication.run(ChooseApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
            sysLogService.writeLog();
    }
}
