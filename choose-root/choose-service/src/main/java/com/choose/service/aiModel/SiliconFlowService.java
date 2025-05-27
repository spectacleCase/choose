package com.choose.service.aiModel;

import com.choose.config.model.AiModelConfig;
import com.choose.config.model.PromptConfig;
import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 硅基流动 - deepSeek的视觉模型
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/1/9 21:04
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SiliconFlowService implements ModelService<Object> {
    private final AiModelConfig modelProperties;
    private final PromptConfig promptConfig;


    @Override
    public String process(String fun,String ...input) {
        String prompt = promptConfig.getPrompt("siliconFlow");
        log.info(modelProperties.getModels().get("siliconFlow").getName());

        // 配置 OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        String content = "";
        // 构建 JSON 请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "Qwen/QVQ-72B-Preview");

        JsonArray messagesArray = getJsonElements(prompt,input[0]);
        requestBody.add("messages", messagesArray);

        requestBody.addProperty("stream", false);
        requestBody.addProperty("max_tokens", 512);
        requestBody.add("stop", JsonNull.INSTANCE);
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("top_p", 0.7);
        requestBody.addProperty("top_k", 50);
        requestBody.addProperty("frequency_penalty", 0.5);
        requestBody.addProperty("n", 1);

        // 明确指定返回格式为 JSON
        JsonObject responseFormat = new JsonObject();
        responseFormat.addProperty("type", "json_object");
        requestBody.add("response_format", responseFormat);

        // 创建请求
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(requestBody.toString(), mediaType);
        Request request = new Request.Builder()
                .url(modelProperties.getModels().get("siliconFlow").getApiUrl())
                .post(body)
                .addHeader("Authorization", "Bearer " + modelProperties.getModels().get("siliconFlow").getApiKey())
                .addHeader("Content-Type", "application/json")
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }

            // 解析响应
            String responseBody = null;
            if (response.body() != null) {
                responseBody = response.body().string();
            }
            log.info("原始响应：{}", responseBody);

            // 提取JSON部分
            JsonObject responseJson;
            JsonArray choices = null;
            if (responseBody != null) {
                responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
                choices = responseJson.getAsJsonArray("choices");
            }

            if (choices != null && !choices.isEmpty()) {
                JsonObject choice = choices.get(0).getAsJsonObject();
                JsonObject messageResponse = choice.getAsJsonObject("message");
                content = messageResponse.get("content").getAsString();
                log.info("食物信息：{}", content);
            }
        } catch (IOException | JsonSyntaxException e) {
            log.error(e.getMessage());
        }


        return content;
    }

    private static @NotNull JsonArray getJsonElements(String prompt,String url) {
        if(url == null || url.isEmpty()) {
            throw new IllegalArgumentException("图片不存在");
        }
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");

        JsonObject textContent = new JsonObject();
        textContent.addProperty("type", "text");
        textContent.addProperty("text", prompt);

        JsonObject imageContent = new JsonObject();
        imageContent.addProperty("type", "image_url");
        JsonObject imageUrl = new JsonObject();
        imageUrl.addProperty("url", url);
        imageUrl.addProperty("detail", "auto");
        imageContent.add("image_url", imageUrl);

        JsonArray contentArray = new JsonArray();
        contentArray.add(textContent);
        contentArray.add(imageContent);
        message.add("content", contentArray);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(message);
        return messagesArray;
    }
}