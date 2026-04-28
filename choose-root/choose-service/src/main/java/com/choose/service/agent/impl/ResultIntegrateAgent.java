package com.choose.service.agent.impl;

import com.choose.service.agent.core.BaseAgent;
import com.choose.service.agent.prompt.PromptTemplates;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ResultIntegrateAgent extends BaseAgent {

    @Override public String name() { return "ResultIntegrateAgent"; }

    @Override
    protected String systemPromptHeader() { return PromptTemplates.RESULT_INTEGRATE; }

    @Override
    protected List<String> allowedTools() { return Collections.emptyList(); }

    @Override
    protected int maxIterations() { return 2; }
}
