package com.choose.service.agent.impl;

import com.choose.service.agent.core.BaseAgent;
import com.choose.service.agent.prompt.PromptTemplates;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DishRetrievalAgent extends BaseAgent {

    @Override public String name() { return "DishRetrievalAgent"; }

    @Override
    protected String systemPromptHeader() { return PromptTemplates.DISH_RETRIEVAL; }

    @Override
    protected List<String> allowedTools() { return List.of("dish_query"); }

    @Override
    protected int maxIterations() { return 3; }
}
