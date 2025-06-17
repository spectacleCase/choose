package com.choose.ai.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2025/6/14 20:21
 */
@EqualsAndHashCode(callSuper=true)
@Data
public class OpenAiSetting extends  CommonAiPlatformSetting{

    @JsonProperty("secret_key")
    private String secretKey;

}
