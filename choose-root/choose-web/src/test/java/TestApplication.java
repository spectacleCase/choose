import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/14 下午10:59
 */

@SpringBootTest
public class TestApplication {

    private static final String MAP_API_KEY = "高德key";

    @Test
    public void text1() {
        System.out.println("nihao ");
    }

    @Test
    public void getLocationInfo() {
        try {
            // 发送HTTP GET请求到ip-api.com
            URL url = new URL("http://ip-api.com/json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 解析JSON响应
            JSONObject jsonObject = new JSONObject(response.toString());
            String city = jsonObject.getString("city");
            String country = jsonObject.getString("country");
            String zip = jsonObject.getString("zip");


            // 输出结果
            System.out.println("City: " + city);
            System.out.println("Country: " + country);
            System.out.println("Zip: " + zip);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void weather() {
        String url = "https://restapi.amap.com/v3/weather/weatherInfo";
        String cityCode = "440900"; // 北京的城市编码
        String parameters = "city=" + cityCode + "&key=" + MAP_API_KEY;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url + "?" + parameters)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);
                // 解析JSON数据并提取你需要的信息
                // 例如，获取当前天气情况
                JSONArray livesArray = jsonObject.getJSONArray("lives");
                if (!livesArray.isEmpty()) {
                    JSONObject liveData = livesArray.getJSONObject(0);
                    String weather = liveData.getString("weather");
                    String temperature = liveData.getString("temperature");
                    String windpower = liveData.getString("windpower");
                    System.out.println("该城市的天气: " + weather);
                    System.out.println("该城市的温度: " + temperature);
                    System.out.println("该城市的风里: " + windpower);
                } else {
                    System.out.println("No weather data found.");
                }
            } else {
                System.out.println("Request failed with code: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void config() {
        Map<String, Weather_tag> weatherMap = readWeatherData("choose-root/choose-web/src/test/java/天气-口味-对应标签表.txt");


        String weather = "大风";
        int temperature = 25 - 2;
        int taste = 2 - 2;
        HashMap<String, Weather_tag> stringWeatherTagHashMap = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                String key = weather + "-" + (temperature + i) + "°C" + "-" + (taste + j);
                Weather_tag weatherTag = weatherMap.get(key);
                if (Objects.isNull(weatherTag)) {
                    continue;
                }
                stringWeatherTagHashMap.put(key, weatherTag);
            }
        }
        // 打印Map中的内容
        for (Map.Entry<String, Weather_tag> entry : stringWeatherTagHashMap.entrySet()) {
            System.out.println("Key: " + entry.getKey());
            Weather_tag weatherTag = entry.getValue();
            System.out.println("Weather: " + weatherTag.weather);
            System.out.println("Temperature: " + weatherTag.temperature);
            System.out.println("Taste: " + weatherTag.taste);
            System.out.println("Distance: " + weatherTag.distance);
            System.out.println("-------------------");
        }
    }

    /**
     * 读取文本的内容
     */
    Map<String, Weather_tag> readWeatherData(String filePath) {
        Map<String, Weather_tag> weatherMap = new HashMap<>();
        File file = new File(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Weather_tag weatherTag = new Weather_tag();
                    weatherTag.weather = parts[0];
                    weatherTag.temperature = parts[1];
                    weatherTag.taste = parts[2];
                    weatherTag.distance = parts[3];

                    String key = weatherTag.weather + "-" + weatherTag.temperature + "-" + weatherTag.taste;
                    weatherMap.put(key, weatherTag);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return weatherMap;
    }

    /**
     * 路径规划
     */
    @Test
    public void pathPlanning() {
        String[] aa = {
                "110.927796,21.679935",
                "110.928983,21.679768",
                "110.929709,21.679139"};
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        for (String string : aa) {
            String s = pathPlanning("110.922615,21.681233", string);
            stringIntegerHashMap.put(string, Integer.parseInt(s));
        }
        Integer minValue = Integer.MAX_VALUE;
        String minKey = null;
        for (Map.Entry<String, Integer> entry : stringIntegerHashMap.entrySet()) {
            if (entry.getValue() < minValue) {
                minValue = entry.getValue();
                minKey = entry.getKey();
            }
        }
        System.out.println(minKey);
        System.out.println(minValue);
    }

    /**
     * 计算最低路径
     * @param origin - 起点坐标
     * @param destination - 终点坐标
     * @return - 消耗时间(s)
     */
    public String pathPlanning(String origin, String destination) {
        try {
            String requestUrl = "https://restapi.amap.com/v3/direction/driving?origin=" +
                    origin + "&destination=" + destination + "&key=" + MAP_API_KEY;

            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject routeObject = jsonObject.getJSONObject("route");
            org.json.JSONArray pathsArray = routeObject.getJSONArray("paths");
            JSONObject pathObject = pathsArray.getJSONObject(0);
            String totalTime = pathObject.getString("duration");
            System.out.println("总时间: " + totalTime + " 秒");
            return totalTime;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

class Weather_tag {
    String weather;

    String temperature;

    String taste;

    String distance;
}
