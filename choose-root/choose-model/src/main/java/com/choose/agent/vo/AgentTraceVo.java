package com.choose.agent.vo;

import com.choose.agent.pojos.AgentCallLog;
import lombok.Data;

import java.util.List;

/**
 * 单次链路的全部Agent调用日志,供管理端Agent监控页使用。
 */
@Data
public class AgentTraceVo {

    private String traceId;
    private List<AgentCallLog> calls;
}
