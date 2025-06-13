package com.choose.service.aiModel;

import com.choose.ai.pojo.AiModel;
import com.choose.constant.AiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/6/13 17:41
 */
@Service
@Slf4j
public class AiModelSettingService {



    private List<AiModel> all =new ArrayList<>();




    public void init(List<AiModel> aiModels) {
        this.all = aiModels;
        initLLMServiceList();
    }


    private synchronized void initLLMServiceList() {
        //dashscope
        initLLMService(AiConstants.ModelPlatform.DASHSCOPE, DashScopeLLMService::new);

        //其他模型...

    }


    private void initLLMService(String platform, Function<AiModel, AbstractLLMService<?>> function) {
        List<AiModel> models = all.stream().filter(
                item -> item.getType().equals(
                        AiConstants.ModelType.TEXT) && item.getPlatform().equals(platform)
                        ).toList();
        if(CollectionUtils.isEmpty(models)) {
            log.warn("{} service is disabled", platform);
        }
        // 创建
        // LLMContext.clearByPlatform(platform);
        for (AiModel model : models) {
            log.info("add llm model,model:{}", model);
            LLMContext.addLLMService(function.apply(model));
        }

    }

}
