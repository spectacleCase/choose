package com.choose.service.agent.impl;

import com.choose.service.agent.core.BaseAgent;
import com.choose.service.agent.prompt.PromptTemplates;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class IntentParseAgent extends BaseAgent {

    @Override public String name() { return "IntentParseAgent"; }

    @Override
    protected String systemPromptHeader() { return PromptTemplates.INTENT_PARSE; }

    @Override
    protected List<String> allowedTools() { return Collections.emptyList(); }

    @Override
    protected int maxIterations() { return 2; }

    /** 不调工具,P-ReAct 没用武之地 */
    @Override
    protected boolean supportsPReAct() { return false; }
}
