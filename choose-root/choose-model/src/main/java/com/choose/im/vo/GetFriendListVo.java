package com.choose.im.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/7 下午4:59
 */
@Data
public class GetFriendListVo {

    private String letter;

    private List<GetFVo> contacts;

}
