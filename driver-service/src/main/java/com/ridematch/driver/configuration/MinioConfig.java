package com.ridematch.driver.configuration;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    private String accessKey;
    private String secretKey;
    private String url;
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        return new MinioClient.Builder().credentials(accessKey, secretKey).endpoint(url).build();
    }
}
