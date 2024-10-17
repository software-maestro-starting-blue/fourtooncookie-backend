package com.startingblue.fourtooncookie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;

@Configuration
public class CloudFrontConfig {

    @Bean
    public CloudFrontClient cloudFrontClient() {
        return CloudFrontClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();
    }
}