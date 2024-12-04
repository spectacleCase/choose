package com.choose.ranking.vo;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/5 下午1:27
 */
@Data
@Builder
public class RankingVo {

    /**
     * 昵称
     */
    private String dishesName;

    /**
     * 图片url
     */
    private String image;

    /**
     * 标签
     */
    private String tag;

    /**
     * 分数
     */
    private Double mark;

    /**
     * 评论总数
     */
    private Integer reviewCount;

    /**
     * 喜欢总数
     */
    private Integer fondCount;
}
