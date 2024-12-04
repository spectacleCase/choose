package com.choose.string;

import java.util.Random;



/**
 * @author 桌角的眼镜
 */
public class StringUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 6;

    private StringUtils() {
        // 私有构造函数防止实例化工具类
    }

    public static String generateRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    // 示例使用
    public static void main(String[] args) {
        String randomString = StringUtils.generateRandomString();
        System.out.println("Random String: " + randomString);
    }
}