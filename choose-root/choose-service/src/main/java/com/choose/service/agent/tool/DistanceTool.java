package com.choose.service.agent.tool;

import com.alibaba.fastjson.JSONObject;
import com.choose.common.CommonUtils;
import com.choose.recommoend.vo.RecommendVo;
import com.choose.service.agent.cache.TwoLevelCache;
import com.choose.service.agent.core.AgentContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 距离计算工具:对ctx.candidates里每条候选,调用高德接口算到用户的距离。
 * 二级缓存命中相同 origin+destination 直接返回,避免重复调高德 API。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DistanceTool implements Tool {

    private final CommonUtils commonUtils;
    private final TwoLevelCache cache;

    private static final String NS = "amap.distance";

    @Override
    public String name() { return "distance_calc"; }

    @Override
    public String description() {
        return "计算用户位置到候选菜品店铺的距离(米)。带二级缓存,相同坐标对直接命中。";
    }

    @Override
    public String parameterSchema() {
        return "{\"origin\":\"用户坐标 lng,lat (可选,默认取ctx.userLocation)\"}";
    }

    @Override
    public String execute(JSONObject params, AgentContext ctx) {
        String origin = params.getString("origin");
        if (origin == null || origin.isBlank()) origin = ctx.getUserLocation();
        if (origin == null || origin.isBlank()) {
            return "用户位置缺失,无法计算距离";
        }
        String originKey = origin;
        int ok = 0;
        for (RecommendVo v : ctx.getCandidates()) {
            String coord = v.getCoordinate();
            if (coord == null || coord.isBlank()) continue;
            String cacheKey = originKey + "|" + coord;
            try {
                String dist = cache.get(NS, cacheKey, String.class,
                        k -> commonUtils.getDistance(originKey, coord));
                if (dist != null) {
                    ctx.getDistanceInfo().put(v.getId(), dist + "米");
                    ok++;
                }
            } catch (Exception e) {
                log.debug("distance calc failed for {}", v.getId(), e);
            }
        }
        StringBuilder sb = new StringBuilder("已计算 ").append(ok).append(" 条距离: ");
        int n = 0;
        for (Map.Entry<String, String> e : ctx.getDistanceInfo().entrySet()) {
            if (n++ > 0) sb.append(", ");
            sb.append(e.getKey()).append("=").append(e.getValue());
            if (n >= 5) { sb.append(" ..."); break; }
        }
        return sb.toString();
    }
}
