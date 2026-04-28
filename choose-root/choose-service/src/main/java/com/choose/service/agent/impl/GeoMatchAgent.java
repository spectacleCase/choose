package com.choose.service.agent.impl;

import com.choose.service.agent.core.BaseAgent;
import com.choose.service.agent.prompt.PromptTemplates;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeoMatchAgent extends BaseAgent {

    @Override public String name() { return "GeoMatchAgent"; }

    @Override
    protected String systemPromptHeader() { return PromptTemplates.GEO_MATCH; }

    @Override
    protected List<String> allowedTools() { return List.of("distance_calc", "geocode"); }

    @Override
    protected int maxIterations() { return 3; }
}
