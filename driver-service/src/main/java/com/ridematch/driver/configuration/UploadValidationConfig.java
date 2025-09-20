package com.ridematch.driver.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "upload")
public class UploadValidationConfig {
    private long maxBytes;
    private int maxImageWidth;
    private int maxImageHeight;
    private long maxImageMegapixels;
    private boolean allowAnimated;
    private int maxPdfPages;
}
