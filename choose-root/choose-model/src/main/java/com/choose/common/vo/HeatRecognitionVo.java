package com.choose.common.vo;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/1/10 22:18
 */
@Data
public class HeatRecognitionVo {

    /**
     * 食物名称
     */
    private String foodName;

    /**
     * 热量（单位：千卡）
     */
    private String calories;

    /**
     * 默认份量（单位：克或毫升）
     */
    private String portionSize;



}
