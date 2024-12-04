package tactics;

import lombok.Data;

// 天气情况（晴天、雨天、雪天）
@Data
public class Context {

    String weather;

    public Context(String weather) {
        this.weather = weather;
    }
}