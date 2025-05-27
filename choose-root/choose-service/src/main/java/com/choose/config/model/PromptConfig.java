package com.choose.config.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 提示词配置
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/1/9 23:04
 */

@Data
@Configuration
public class PromptConfig {

    @Value("${prompt.file-path}")
    private String promptFilePath;

    private JsonObject prompts;

    @PostConstruct
    public void init() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(promptFilePath)) {
            if (inputStream == null) {
                throw new RuntimeException("无法找到提示词文件: " + promptFilePath);
            }
            String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            prompts = JsonParser.parseString(jsonContent).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("加载提示词文件失败", e);
        }
    }

    public String getPrompt(String modelName) {
        if (prompts.has(modelName)) {
            return prompts.getAsJsonObject(modelName).get("prompt").getAsString();
        }
        throw new IllegalArgumentException("未找到模型对应的提示词: " + modelName);
    }
}
