package com.choose.service.agent.tool;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具注册中心。Spring扫描所有Tool实现并以name为key注册。
 */
@Component
public class ToolRegistry {

    private final Map<String, Tool> tools = new HashMap<>();
    private final List<Tool> toolBeans;

    public ToolRegistry(List<Tool> toolBeans) {
        this.toolBeans = toolBeans;
    }

    @PostConstruct
    public void init() {
        for (Tool t : toolBeans) {
            tools.put(t.name(), t);
        }
    }

    public Tool get(String name) {
        return tools.get(name);
    }

    public boolean has(String name) {
        return tools.containsKey(name);
    }

    /**
     * 拼装可见工具集的描述,塞入System Prompt
     */
    public String renderToolsDescription(List<String> allowedToolNames) {
        StringBuilder sb = new StringBuilder();
        for (String name : allowedToolNames) {
            Tool t = tools.get(name);
            if (t == null) continue;
            sb.append("- ").append(t.name()).append(": ").append(t.description()).append("\n")
              .append("  参数: ").append(t.parameterSchema()).append("\n");
        }
        return sb.toString();
    }
}
