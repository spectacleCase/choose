package com.choose.admin.system.server;

import lombok.Data;

/**
 * <p>
 * 系统相关信息
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/7/3 下午9:33
 */
@Data
public class Sys {

    /**
     * 服务器名称
     */
    private String computerName;

    /**
     * 服务器ip
     */
    private String computerIp;

    /**
     * 项目路径
     */
    private String userDir;

    /**
     * 操作系统
     */
    private String osName;

    /**
     * 系统架构
     */
    private String osArch;

}
