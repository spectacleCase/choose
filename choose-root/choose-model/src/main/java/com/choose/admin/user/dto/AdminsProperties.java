package com.choose.admin.user.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/6/26 下午12:47
 */
@Data
@Component
@ConfigurationProperties(prefix = "admins")
public class AdminsProperties {
    private Map<String,User> users;
}
