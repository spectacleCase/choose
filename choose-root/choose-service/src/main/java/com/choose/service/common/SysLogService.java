package com.choose.service.common;

import com.baomidou.mybatisplus.extension.service.IService;
import com.choose.common.SysLog;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/9 下午2:37
 */
public interface SysLogService extends IService<SysLog> {

    void writeLog();
}
