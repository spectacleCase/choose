package com.choose.service.agent.tool;

import com.alibaba.fastjson.JSONObject;
import com.choose.common.CommonUtils;
import com.choose.service.agent.cache.TwoLevelCache;
import com.choose.service.agent.core.AgentContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 地理编码工具:用户位置描述->结构化地址。带二级缓存。
 */
@Component
@RequiredArgsConstructor
public class GeocodeTool implements Tool {

    private final CommonUtils commonUtils;
    private final TwoLevelCache cache;

    private static final String NS = "amap.geocode";

    @Override
    public String name() { return "geocode"; }

    @Override
    public String description() {
        return "将经纬度坐标解析为可读地址。带二级缓存,相同坐标直接命中。";
    }

    @Override
    public String parameterSchema() {
        return "{\"location\":\"lng,lat 经纬度坐标\"}";
    }

    @Override
    public String execute(JSONObject params, AgentContext ctx) {
        String location = params.getString("location");
        if (location == null || location.isBlank()) location = ctx.getUserLocation();
        if (location == null || location.isBlank()) {
            return "未提供坐标";
        }
        try {
            String key = location;
            String addr = cache.get(NS, key, String.class, k -> commonUtils.geocode(key));
            return "地址: " + addr;
        } catch (Exception e) {
            return "geocode失败: " + e.getMessage();
        }
    }
}
