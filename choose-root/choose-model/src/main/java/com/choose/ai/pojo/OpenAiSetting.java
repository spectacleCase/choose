package com.choose.ai.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonProperty;

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
