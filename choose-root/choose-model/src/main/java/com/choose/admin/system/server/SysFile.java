package com.choose.admin.system.server;

import lombok.Data;

/**
 * <p>
 * 系统文件相关信息
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/7/3 下午9:35
 */
@Data
public class SysFile {

    /**
     * 盘符路径
     */
    private String dirName;

    /**
     * 盘符类型
     */
    private String sysTypeName;

    /**
     * 文件类型
     */
    private String typeName;

    /**
     * 总大小
     */
    private String total;

    /**
     * 剩余大小
     */
    private String free;

    /**
     * 已经使用量
     */
    private String used;

    /**
     * 资源使用率
     */
    private double usage;
}
