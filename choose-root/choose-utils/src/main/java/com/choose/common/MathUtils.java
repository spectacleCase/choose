package com.choose.common;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 上午1:40
 */
public class MathUtils {

    /**
     * 求平均分
     * null 则返回0
     *
     * @return - 平均分
     */
    public static double average(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            return 0;
        }
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return (double) sum / numbers.length;
    }

    /**
     * 总分
     * a - 30% b - 70%
     */
    public static double score(Double a, Double b) throws Exception {
        if (a == null || b == null) {
            // throw new CustomException(AppHttpCodeEnum.DATA_PROBLEM);
            throw new Exception();
        }
        return a * 0.3 + b * 0.7;
    }

}
