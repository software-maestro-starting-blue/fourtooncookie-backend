package com.startingblue.fourtooncookie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

@Configuration
public class LambdaConfig {

    private static final Region region = Region.AP_NORTHEAST_2;

    @Bean
    public LambdaClient lambdaClient() {
        return LambdaClient.builder()
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder()
                                .retryPolicy(RetryPolicy.none())
                                .build()
                )
                .region(region)
                .build();
    }
}
