package com.eappcat.llm.fastllmapi.props;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "fastllm")
@Data
@Component
public class FastllmConfigProperties {
    private String url="http://fastllm-web:8080";
    private String embeddingUrl="http://m3e-api:8080/m3e";
    private String qwenUrl="http://qwen-vl:8080";
}
