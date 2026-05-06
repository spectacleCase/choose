package com.choose.service.agent.log;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.choose.agent.pojos.AgentCallLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Agent 调用聚合统计 (论文 5.2 监控页 4 类指标)。
 */
@Service
@RequiredArgsConstructor
public class AgentStatsService {

    private final AgentCallLogService logService;

    public Map<String, Object> overview(Date from, Date to) {
        List<AgentCallLog> rows = scan(from, to);
        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("frequency", frequency(rows));
        ret.put("avgLatency", avgLatency(rows));
        ret.put("successRate", successRate(rows));
        ret.put("tokenTrend", tokenTrend(rows));
        ret.put("totalCalls", rows.size());
        return ret;
    }

    /** 各 Agent 调用次数 */
    public Map<String, Long> frequency(List<AgentCallLog> rows) {
        Map<String, Long> m = new LinkedHashMap<>();
        for (AgentCallLog r : rows) {
            m.merge(r.getAgentName(), 1L, Long::sum);
        }
        return m;
    }

    /** 各 Agent 平均响应耗时(ms) */
    public Map<String, Double> avgLatency(List<AgentCallLog> rows) {
        Map<String, long[]> acc = new LinkedHashMap<>();
        for (AgentCallLog r : rows) {
            if (r.getElapsedMs() == null) continue;
            long[] v = acc.computeIfAbsent(r.getAgentName(), k -> new long[2]);
            v[0] += r.getElapsedMs();
            v[1] += 1;
        }
        Map<String, Double> ret = new LinkedHashMap<>();
        for (Map.Entry<String, long[]> e : acc.entrySet()) {
            ret.put(e.getKey(), e.getValue()[1] == 0 ? 0.0
                    : Math.round((double) e.getValue()[0] / e.getValue()[1] * 100.0) / 100.0);
        }
        return ret;
    }

    /** 各 Agent 成功率 0-1 */
    public Map<String, Double> successRate(List<AgentCallLog> rows) {
        Map<String, long[]> acc = new LinkedHashMap<>();
        for (AgentCallLog r : rows) {
            long[] v = acc.computeIfAbsent(r.getAgentName(), k -> new long[2]);
            v[1] += 1;
            if ("SUCCESS".equals(r.getStatus())) v[0] += 1;
        }
        Map<String, Double> ret = new LinkedHashMap<>();
        for (Map.Entry<String, long[]> e : acc.entrySet()) {
            ret.put(e.getKey(), e.getValue()[1] == 0 ? 0.0
                    : Math.round((double) e.getValue()[0] / e.getValue()[1] * 10000.0) / 10000.0);
        }
        return ret;
    }

    /** 按天 token 消耗趋势 yyyy-MM-dd -> 总 token */
    public Map<String, Long> tokenTrend(List<AgentCallLog> rows) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Long> m = new TreeMap<>();
        for (AgentCallLog r : rows) {
            if (r.getCreateTime() == null) continue;
            String d = fmt.format(r.getCreateTime());
            long t = r.getTokenUsage() == null ? 0 : r.getTokenUsage();
            m.merge(d, t, Long::sum);
        }
        return m;
    }

    /** 推荐效果分析: 每日推荐请求量 (按 IntentParseAgent 计) */
    public Map<String, Long> dailyRequestCount(Date from, Date to) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        List<AgentCallLog> rows = scan(from, to);
        Map<String, Long> m = new TreeMap<>();
        for (AgentCallLog r : rows) {
            if (!"IntentParseAgent".equals(r.getAgentName())) continue;
            if (r.getCreateTime() == null) continue;
            m.merge(fmt.format(r.getCreateTime()), 1L, Long::sum);
        }
        return m;
    }

    /** 每日端到端响应耗时(按 traceId 求和后取均值) */
    public Map<String, Double> dailyAvgResponseTime(Date from, Date to) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        List<AgentCallLog> rows = scan(from, to);
        // day -> traceId -> sumMs
        Map<String, Map<String, Long>> byDayTrace = new TreeMap<>();
        for (AgentCallLog r : rows) {
            if (r.getCreateTime() == null || r.getElapsedMs() == null) continue;
            String d = fmt.format(r.getCreateTime());
            byDayTrace.computeIfAbsent(d, k -> new LinkedHashMap<>())
                    .merge(r.getTraceId(), r.getElapsedMs(), Long::sum);
        }
        Map<String, Double> ret = new TreeMap<>();
        for (Map.Entry<String, Map<String, Long>> e : byDayTrace.entrySet()) {
            Map<String, Long> traces = e.getValue();
            long sum = traces.values().stream().mapToLong(Long::longValue).sum();
            ret.put(e.getKey(), traces.isEmpty() ? 0.0
                    : Math.round((double) sum / traces.size() * 100.0) / 100.0);
        }
        return ret;
    }

    private List<AgentCallLog> scan(Date from, Date to) {
        LambdaQueryWrapper<AgentCallLog> qw = new LambdaQueryWrapper<>();
        if (from != null) qw.ge(AgentCallLog::getCreateTime, from);
        if (to != null) qw.le(AgentCallLog::getCreateTime, to);
        qw.last("LIMIT 5000");
        return logService.list(qw);
    }
}
