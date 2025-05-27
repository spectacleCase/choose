package com.choose.config.model;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author lizhentao
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "model")
public class AiModelConfig {
    private Map<String, ModelProperties> models;

    @Data
    public static class ModelProperties {
        private String name;
        private String apiUrl;
        private String apiKey;
    }
}
