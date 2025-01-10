package com.choose.service.common.impl;



import com.choose.dishes.pojos.FoodsHeat;
import com.choose.mapper.FooHeatMapper;
import okhttp3.OkHttpClient;
import org.jsoup.Jsoup;
import okhttp3.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 食物热量爬虫
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/1/10 23:07
 */
@Service
public class FoodCrawlerService {

    @Resource
    private FooHeatMapper fooHeatMapper;

    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * 从网站获取食物数据
     */
    public void fetchFoodData(String keyword) {
        String url = "https://www.boohee.com/food/search";
        String htmlContent = fetchHtml(url, keyword);

        if (htmlContent != null) {
            insertFoodData(parseHtml(htmlContent));
        }
        // return new ArrayList<>();
    }

    /**
     * 使用 OkHttp 发送 HTTP 请求获取 HTML 内容
     */
    private String fetchHtml(String url, String keyword) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        urlBuilder.addQueryParameter("keyword", keyword);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析 HTML 内容
     */
    private List<FoodsHeat> parseHtml(String htmlContent) {
        List<FoodsHeat> foods = new ArrayList<>();
        Document doc = Jsoup.parse(htmlContent);
        Elements foodItems = doc.select("div.text-box.pull-left");

        for (Element item : foodItems) {
            String name = item.select("a").text().trim();
            String calorieText = item.select("p").text().trim();

            // 解析热量和比例
            String[] parts = calorieText.split(" 大卡\\(每");
            if (parts.length == 2) {
                double calories = Double.parseDouble(parts[0].replace("热量：", ""));
                int proportion = Integer.parseInt(parts[1].replace("克)", ""));

                // 解析别名
                String[] nameParts = name.split("，又叫");
                String mainName = nameParts[0];
                String alias = nameParts.length > 1 ? nameParts[1] : null;

                // 创建 Food 对象
                FoodsHeat food = new FoodsHeat();
                food.setName(mainName);
                food.setAlias(alias);
                food.setCalories((int) calories);
                food.setProportion(proportion);

                foods.add(food);
            }
        }

        return foods;
    }

    /**
     * 插入食物数据到数据库
     */
    public void insertFoodData(List<FoodsHeat> foods) {
        for (FoodsHeat food : foods) {
            fooHeatMapper.insert(food);
        }

    }

    /**
     * 查询食物数据
     */
    public List<FoodsHeat> searchFood(String keyword) {
        // return fooHeatMapper.findByNameOrAliasContaining(keyword, keyword);
        return new ArrayList<>();
    }
}
