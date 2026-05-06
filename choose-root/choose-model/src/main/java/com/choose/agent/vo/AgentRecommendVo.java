package com.choose.agent.vo;

import com.choose.recommoend.vo.RecommendVo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Agent协作推荐响应。除了推荐列表,还附带trace/intent等可观测字段。
 */
@Data
public class AgentRecommendVo {

    /** 链路ID,用于查询调用日志 */
    private String traceId;

    /** 是否检测到矛盾约束 */
    private boolean conflictDetected;

    /** 追问内容(若检测到矛盾) */
    private String clarifyQuestion;

    /** 意图解析的结构化结果 */
    private Object parsedIntent;

    /** 最终推荐 */
    private List<RecommendVo> items;

    /** 各Agent耗时 ms */
    private Map<String, Long> agentLatency;

    /** 是否走了异步营养路径,前端见此为 true 时启动轮询 GET enriched */
    private boolean asyncNutritionPending;

    /** P-ReAct 累计节省的端到端耗时 ms (论文 2.2.4) */
    private long pReActSavedMs;

    /** 是否被 PromptGuard 拦截 (论文 2.1.3 三层防护) */
    private boolean blocked;

    /** 友好拒绝文案,blocked=true 时填充 */
    private String blockReason;
}
