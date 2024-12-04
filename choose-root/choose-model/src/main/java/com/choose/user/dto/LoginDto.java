package com.choose.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 上午1:08
 */
@Data
public class LoginDto {
    @NotBlank(message = "code不允许为空")
    private String code;
}
