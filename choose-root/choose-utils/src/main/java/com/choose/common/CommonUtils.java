package com.choose.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/15 上午1:57
 */
@Component
@Slf4j
public class CommonUtils {

    private static String MAP_API_KEY;
    private static String COMPRESSED_URL;
    private static final String IP_URL = "https://restapi.amap.com/v3/ip";
    private static final String ADCODE = "adcode";

    @Value("${system.map_api_key}")
    public void setMapApiKey(String mapApiKey) {
        MAP_API_KEY = mapApiKey;
    }

    @Value("${system.compressed_url}")
    public void setCompressedUrl(String compressedUrl) {
        COMPRESSED_URL = compressedUrl;
    }


    /**
     * 根据城市获取天气
     *
     * @return map 天气，温度，风力
     */
    public static HashMap<String, String> getWeather() {
        String url = "https://restapi.amap.com/v3/weather/weatherInfo";
        String cityCode = "440900";
        String parameters = "city=" + cityCode + "&key=" + MAP_API_KEY;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url + "?" + parameters)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonData = Objects.requireNonNull(response.body()).string();
                JSONObject jsonObject = new JSONObject(jsonData);
                // 获取当前天气,温度，风力
                JSONArray livesArray = jsonObject.getJSONArray("lives");
                if (!livesArray.isEmpty()) {
                    JSONObject liveData = livesArray.getJSONObject(0);
                    String weather = liveData.getString("weather");
                    String temperature = liveData.getString("temperature");
                    String windpower = liveData.getString("windpower");
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    stringStringHashMap.put("weather", weather);
                    stringStringHashMap.put("temperature", temperature);
                    stringStringHashMap.put("windpower", windpower);
                    return stringStringHashMap;
                } else {
                    log.warn("No weather data found.");
                }
            } else {
                log.error("Request failed with code: {}", response.code());
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;

    }

    /**
     * 获取地址
     * @return 地址
     */
    public static String geocode(String location) {
        String url = "https://restapi.amap.com/v3/geocode/regeo";
        String parameters = "location=" + location + "&key=" + MAP_API_KEY;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url + "?" + parameters)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonData = Objects.requireNonNull(response.body()).string();
                JSONObject jsonObject = new JSONObject(jsonData);
                return jsonObject.getJSONObject("regeocode").get("formatted_address").toString();

            }
        }catch (Exception e) {

        }
        return "";
    }

    /**
     * 计算两个坐标之间的距离
     *
     * @param origin     起点坐标，格式为 "经度,纬度"
     * @param destination 终点坐标，格式为 "经度,纬度"
     * @return 两点之间的距离（米）
     */
    public static String getDistance(String origin, String destination) {
        try {
            // 构建请求 URL
            String url = "https://restapi.amap.com/v3/distance";
            String parameters = "key=" + MAP_API_KEY + "&origins=" + origin + "&destination=" + destination;
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url + "?" + parameters)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String jsonData = Objects.requireNonNull(response.body()).string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray resultsArray = jsonObject.getJSONArray("results");
                    JSONObject firstResult = resultsArray.getJSONObject(0);
                    String distance = firstResult.getString("distance");
                    return "距离：" + distance + " 米";
                }
            }catch (Exception e) {

            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 通过ip获取城市代码
     *
     * @param ip ip地址
     * @return 返回值
     */
    @Deprecated
    public static String getAdcodeByIp(String ip) {
        String url = String.format("%s?key=%s&ip=%s", IP_URL, MAP_API_KEY, ip);
//        log.info("高德获取adcode;请求url:{},请求人：{}", url, UserLocalThread.getUser().getId());

        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("高德通过ip获取adcode时的返回值：{}", response);
               throw new Exception();
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
               throw new Exception();
            }
            String body = responseBody.string();
            Map<String, Object> map = objectMapper.readValue(body, Map.class);
            System.out.println(map.get(ADCODE));
            if (map.get(ADCODE) instanceof List) {
               throw new Exception();
            }
            return (String) map.get(ADCODE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算起点到各个终点的的最低地点
     *
     * @param origin       - 起点
     * @param destinations - 多个终点
     * @return - minKey最短地点 origin消耗时间
     */
    public static String[] pathPlanning(String origin, List<String> destinations) {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        for (String string : destinations) {
            String s = pathPlanning(origin, string);
            if (Objects.nonNull(s)) {
                stringIntegerHashMap.put(string, Integer.parseInt(s));
            }
        }
        Integer minValue = Integer.MAX_VALUE;
        String minKey = null;
        for (Map.Entry<String, Integer> entry : stringIntegerHashMap.entrySet()) {
            if (entry.getValue() < minValue) {
                minValue = entry.getValue();
                minKey = entry.getKey();
            }
        }
        return new String[]{minKey, minValue.toString()};
    }

    /**
     * 计算最低路径
     *
     * @param origin      - 起点坐标
     * @param destination - 终点坐标
     * @return - 消耗时间(s)
     */
    private static String pathPlanning(String origin, String destination) {
        String requestUrl = "https://restapi.amap.com/v3/direction/driving?origin=" +
                origin + "&destination=" + destination + "&key=" + MAP_API_KEY;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(requestUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONObject routeObject = jsonObject.getJSONObject("route");
                JSONArray pathsArray = routeObject.getJSONArray("paths");
                JSONObject pathObject = pathsArray.getJSONObject(0);
                return pathObject.getString("duration");
            } else {
                log.warn("Request failed: {}", response.code());
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
        return null;
    }


    /**
     * 获取客户端的 IP 地址
     *
     * @param request HttpServletRequest
     * @return 客户端的 IP 地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /**
     * 压缩图片
     */
    public static File compressImage(File imageFile) {
        File compressedFile = null;
        try {
            compressedFile = File.createTempFile("compressed_", ".jpg", new File(COMPRESSED_URL));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream payload = new FileInputStream(imageFile);
            // scale: 设置缩略图的缩放系数, 大于0.0
            // outputQuality: 设置将缩略图写入外部目标(如文件或输出流)时用于压缩缩略图的压缩算法的输出质量, 该值是一个介于0.0f和1.0f之间的浮点数，其中0.0f表示最低质量，1.0f表示压缩编解码器应该使用的最大质量设置。
            Thumbnails.of(payload).scale(1.0f).outputQuality(0.5f).toFile(compressedFile);
        } catch (IOException e) {
            log.error("Compress image failed. Exception: {}", e.getMessage());
        }
        return compressedFile;
    }
}
