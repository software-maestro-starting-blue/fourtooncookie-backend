package com.startingblue.fourtooncookie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SQSConfig {

    private static final Region region = Region.AP_NORTHEAST_2;


    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(region)
                .build();
    }
}
