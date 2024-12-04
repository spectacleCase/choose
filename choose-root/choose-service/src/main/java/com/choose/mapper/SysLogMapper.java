package com.choose.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.choose.common.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/9 下午2:01
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {
    Double getAverageResponseTimeToday();
}
