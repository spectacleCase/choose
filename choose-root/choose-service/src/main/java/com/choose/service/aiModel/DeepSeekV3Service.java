package com.choose.service.aiModel;

import com.choose.config.model.AiModelConfig;
import com.choose.config.model.PromptConfig;
import com.choose.constant.CommonConstants;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.exception.CustomException;
import com.choose.service.textAbstract.SseEmitterUTF8;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import okio.Okio;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

/**
 * <p>
 * DeepSeekV3模型
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/1/9 21:04
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DeepSeekV3Service<T> implements ModelService<Object> {

    public interface functionName  {
        String GET_HEALTH_TIPS = "getHealthTips";
        String REVIEW_RATING = "reviewRating";
    };

    private final AiModelConfig modelProperties;
    private final PromptConfig promptConfig;
    private String reviewRating;
    private String healthTipsRole;
    private String healthTipsContent;

    @PostConstruct
    public void init(){
        String prompt = promptConfig.getPrompt("deepSeekV3");
        JsonObject asJsonObject = JsonParser.parseString(prompt).getAsJsonObject();
        reviewRating = asJsonObject.get("reviewRating").getAsString();
        healthTipsRole = asJsonObject.get("healthTipsRole").getAsString();
        healthTipsContent = asJsonObject.get("healthTipsContent").getAsString();

    }

    @Override
    public T process(String fun,String ...input) {
         switch (fun) {
            case "getHealthTips" -> {
                String health;
                try{
                    health = getHealthTips().toString();
                }catch (Exception e) {
                    health = CommonConstants.healthTips.health[new Random().nextInt(10)];
                }

                return (T) health;
            }
            case "reviewRating" -> {
                Double v;
                try{
                    v = reviewRating(input[0]);
                } catch (Exception e) {
                    v = 0.0;
                }

                return (T) v;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * 评论评分
     */
    private Double reviewRating(String content) {
        StringBuilder extracted = extracted(reviewRating, content);
        if (Objects.isNull(extracted)) {
            return (double) 0;
        }
        return Double.parseDouble(extracted.toString());
    }

    /**
     * 获取健康小知识
     */
    private StringBuilder getHealthTips() {
        return extracted(healthTipsRole, healthTipsContent);
    }

    /**
     * ai获取内容
     *
     * @param role    - 角色定位
     * @param content - 询问内容
     * @return 结果
     */
    public  StringBuilder extracted(String role, String content) {
        SseEmitterUTF8 sseEmitter = new SseEmitterUTF8(-1L);
        StringBuilder res = new StringBuilder();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        if (role.isEmpty()) {
            role = "You are a helpful assistant";
        }
        MediaType mediaType = MediaType.parse("application/json");
        String meg = getString(role, content);
        RequestBody body = RequestBody.create(mediaType, meg);
        Request request = new Request.Builder()
                .url(modelProperties.getModels().get("deepSeekV3").getApiUrl())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + modelProperties.getModels().get("deepSeekV3").getApiKey())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    try (BufferedSource source = Okio.buffer(responseBody.source())) {
                        String line;
                        while ((line = source.readUtf8Line()) != null) {
                            if (line.isEmpty() || -1 == line.indexOf('{')) {
                                continue;
                            }
                            String jsonStr = line.substring(line.indexOf('{'));
                            try {
                                JSONObject jsonObj = new JSONObject(jsonStr);
                                JSONArray choices = jsonObj.getJSONArray("choices");
                                JSONObject choice = choices.getJSONObject(0);
                                JSONObject delta = choice.getJSONObject("delta");
                                String responseContent = delta.getString("content");
                                sseEmitter.send(responseContent);
                                res.append(responseContent);
                            } catch (Exception e) {
                                log.error("ai bug", e);
                            }
                        }
                    }
//                    log.info("user: {} post AiContent: {}", UserLocalThread.getUser().getId(), res);
                    return res;
                }
            } else {
                // 如果请求失败，打印出错误信息
                log.error("Request failed with code: {}", response.code());
            }
        } catch (IOException e) {
            log.error("Request failed with code: ", e);
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
        return null;
    }

    private static String getString(String role, String content) {
        String meg = """
                {
                  "messages": [
                    {
                      "content": "%s",
                      "role": "system"
                    },
                    {
                      "content": "%s",
                      "role": "user"
                    }
                  ],
                  "model": "deepseek-chat",
                  "frequency_penalty": 0,
                  "max_tokens": 1024,
                  "presence_penalty": 2.0,
                  "stop": null,
                  "stream": true,
                  "temperature": 1,
                  "top_p": 1,
                  "logprobs": false,
                  "top_logprobs": null
                }""";
        meg = String.format(meg, role, content);
        return meg;
    }

}