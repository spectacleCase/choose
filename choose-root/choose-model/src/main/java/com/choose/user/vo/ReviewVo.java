package com.choose.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/7 下午8:11
 */
@Data
public class ReviewVo{

    private Long id;

    private Long dishesId;

    private Long userId;

    private String review;

    private String avatar;

    private String nickname;

    private Date createTime;

}
