package com.choose.math;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 数学工具类
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/14 下午10:20
 */
public class MathUtils {

    /**
     * 计算两个向量的余弦相似度
     * @param vector1 第一个向量
     * @param vector2 第二个向量
     * @return 余弦相似度
     */
    public static double cosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2) {
        Set<String> commonItems = new HashSet<>(vector1.keySet());
        commonItems.retainAll(vector2.keySet());
        if (commonItems.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String itemId : commonItems) {
            double rating1 = vector1.get(itemId);
            double rating2 = vector2.get(itemId);
            dotProduct += rating1 * rating2;
            norm1 += rating1 * rating1;
            norm2 += rating2 * rating2;
        }
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

}
