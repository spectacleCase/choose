// package com.choose.service.textAbstract;
//
//
// import com.choose.enums.AppHttpCodeEnum;
// import com.choose.exception.CustomException;
// import lombok.extern.slf4j.Slf4j;
// import okhttp3.*;
// import okio.BufferedSource;
// import okio.Okio;
// import org.json.JSONArray;
// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
//
// import java.io.IOException;
// import java.util.Objects;
//
// /**
//  * <p>
//  * 文本转换
//  * </p>
//  * <b>对接deepSeek大模型</b>
//  *
//  * @author 桌角的眼镜
//  * @version 1.0
//  * @since 2024/5/7 上午10:56
//  */
// @Slf4j
// @Component
// public class TestTextAbstract {
//
//     private static  String API_KEY;
//
//     private static final String REVIEW_RATING = "你是一个美食评论家，从里面的评论中，在1-100分中，只要给出分数即可，不要其他文字";
//
//
//     @Value("${system.deep_seek_api_key}")
//     public void setApiKey(String apiKey) {
//         API_KEY = apiKey;
//     }
//
//     /**
//      * 评论评分
//      */
//     public static double reviewRating(String content) {
//         StringBuilder extracted = extracted(REVIEW_RATING, content);
//         if (Objects.isNull(extracted)) {
//             return 0;
//         }
//         return Double.parseDouble(extracted.toString());
//     }
//
//
//     /**
//      * ai获取内容
//      *
//      * @param role    - 角色定位
//      * @param content - 询问内容
//      * @return 结果
//      */
//     public static StringBuilder extracted(String role, String content) {
//         SseEmitterUTF8 sseEmitter = new SseEmitterUTF8(-1L);
//         StringBuilder res = new StringBuilder();
//         OkHttpClient client = new OkHttpClient().newBuilder()
//                 .build();
//         if (role.isEmpty()) {
//             role = "You are a helpful assistant";
//         }
//         MediaType mediaType = MediaType.parse("application/json");
//         String meg = getString(role, content);
//         RequestBody body = RequestBody.create(mediaType, meg);
//         Request request = new Request.Builder()
//                 .url("https://api.deepseek.com/chat/completions")
//                 .method("POST", body)
//                 .addHeader("Content-Type", "application/json")
//                 .addHeader("Accept", "application/json")
//                 .addHeader("Authorization", "Bearer " + API_KEY)
//                 .build();
//         try (Response response = client.newCall(request).execute()) {
//             if (response.isSuccessful()) {
//                 ResponseBody responseBody = response.body();
//                 if (responseBody != null) {
//                     try (BufferedSource source = Okio.buffer(responseBody.source())) {
//                         String line;
//                         while ((line = source.readUtf8Line()) != null) {
//                             if (line.isEmpty() || -1 == line.indexOf('{')) {
//                                 continue;
//                             }
//                             String jsonStr = line.substring(line.indexOf('{'));
//                             try {
//                                 JSONObject jsonObj = new JSONObject(jsonStr);
//                                 JSONArray choices = jsonObj.getJSONArray("choices");
//                                 JSONObject choice = choices.getJSONObject(0);
//                                 JSONObject delta = choice.getJSONObject("delta");
//                                 String responseContent = delta.getString("content");
//                                 sseEmitter.send(responseContent);
//                                 res.append(responseContent);
//                             } catch (Exception e) {
//                                 log.error("ai bug", e);
//                             }
//                         }
//                     }
// //                    log.info("user: {} post AiContent: {}", UserLocalThread.getUser().getId(), res);
//                     return res;
//                 }
//             } else {
//                 // 如果请求失败，打印出错误信息
//                 log.error("Request failed with code: {}", response.code());
//             }
//         } catch (IOException e) {
//             log.error("Request failed with code: ", e);
//             throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
//         }
//         return null;
//     }
//
//     private static String getString(String role, String content) {
//         String meg = """
//                 {
//                   "messages": [
//                     {
//                       "content": "%s",
//                       "role": "system"
//                     },
//                     {
//                       "content": "%s",
//                       "role": "user"
//                     }
//                   ],
//                   "model": "deepseek-chat",
//                   "frequency_penalty": 0,
//                   "max_tokens": 1024,
//                   "presence_penalty": 2.0,
//                   "stop": null,
//                   "stream": true,
//                   "temperature": 1,
//                   "top_p": 1,
//                   "logprobs": false,
//                   "top_logprobs": null
//                 }""";
//         meg = String.format(meg, role, content);
//         return meg;
//     }
// }
