package tactics;

import lombok.Data;

/**
 * 食物类型及id
 */
@Data
public class Food {
    String id;
    String type; // 食物类别

    public Food(String id, String type) {
        this.id = id;
        this.type = type;
    }
}

