package com.startingblue.fourtooncookie.aws.sqs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SQSConfig {

    private static final Region region = Region.AP_NORTHEAST_2;

    @Bean
    public SqsAsyncClient sqsClient() {
        return SqsAsyncClient.builder()
                .region(region)
                .build();
    }
}
