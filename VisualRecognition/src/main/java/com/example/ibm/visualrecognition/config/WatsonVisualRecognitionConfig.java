package com.example.ibm.visualrecognition.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author John
 * @create 2019/7/15 10:35
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "watson")
public class WatsonVisualRecognitionConfig {
    private String apikey;
    private String version;
    private String endpoint;
    private String negativeExamplesPath;
}
