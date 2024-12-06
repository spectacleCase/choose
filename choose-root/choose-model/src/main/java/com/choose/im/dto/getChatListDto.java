package com.choose.im.dto;

import com.choose.common.CommentPageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/12/6 下午10:14
 */
@Data
public class getChatListDto {
    private String id;
    private String lastCreateTime;
}
