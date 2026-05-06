package com.choose.service.agent.core;

import com.alibaba.fastjson.JSONObject;
import com.choose.recommoend.vo.RecommendVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话级上下文，所有Agent共享读写。AgentCoordinator在请求开始时实例化一次。
 */
@Data
public class AgentContext {

    /** 本次推荐的会话/链路ID */
    private final String traceId = UUID.randomUUID().toString().replace("-", "");

    /** 用户原始自然语言查询 */
    private String userQuery;

    /** 用户ID */
    private String userId;

    /** 用户当前坐标 "lng,lat" */
    private String userLocation;

    /** 期望返回数量 */
    private int topK = 5;

    /** 意图解析Agent输出: 结构化约束 */
    private JSONObject parsedIntent;

    /** 是否检测到矛盾约束并需追问 */
    private boolean conflictDetected;
    private String clarifyQuestion;

    /** 候选菜品(由DishRetrievalAgent填充) */
    private List<RecommendVo> candidates = new ArrayList<>();

    /** 营养评分 dishId -> 0-100 */
    private final Map<String, Double> nutritionScores = new ConcurrentHashMap<>();

    /** 距离信息 dishId -> 距离描述 */
    private final Map<String, String> distanceInfo = new ConcurrentHashMap<>();

    /** 最终推荐结果 */
    private List<RecommendVo> finalRecommendations = new ArrayList<>();

    /** 各Agent的ReAct trace,key=AgentName */
    private final Map<String, List<ReActStep>> agentTraces = new ConcurrentHashMap<>();

    /** 各Agent耗时,key=AgentName */
    private final Map<String, Long> agentLatency = new ConcurrentHashMap<>();

    /** 多轮追问历史 (论文 3.1.4 多轮对话) */
    private List<ClarifyTurn> clarifyHistory = new ArrayList<>();

    public void addTrace(String agentName, List<ReActStep> steps) {
        agentTraces.put(agentName, steps);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClarifyTurn {
        private String question;
        private String reply;
    }
}
