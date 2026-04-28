package com.choose.service.agent.impl;

import com.choose.service.agent.core.BaseAgent;
import com.choose.service.agent.prompt.PromptTemplates;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NutritionAgent extends BaseAgent {

    @Override public String name() { return "NutritionAgent"; }

    @Override
    protected String systemPromptHeader() { return PromptTemplates.NUTRITION; }

    @Override
    protected List<String> allowedTools() { return List.of("nutrition_lookup", "nutrition_estimate"); }

    @Override
    protected int maxIterations() { return 3; }
}
