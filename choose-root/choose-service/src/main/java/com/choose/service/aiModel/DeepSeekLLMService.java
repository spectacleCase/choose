package com.choose.service.aiModel;

import com.choose.constant.AiConstants;
import com.choose.ai.pojo.AiModel;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/6/14 20:18
 */
@Slf4j
@Accessors(chain = true)
public class DeepSeekLLMService extends OpenAiLLMService{

    public DeepSeekLLMService(AiModel model) {
        // super(model,AiConstants.SysConfigKey.DEEPSEEK_SETTING);
        super(model);
    }
}
