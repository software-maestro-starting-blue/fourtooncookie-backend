package com.startingblue.fourtooncookie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;

import java.time.Duration;

import java.time.Duration;

@Configuration
public class LambdaConfig {

    private static final Region region = Region.AP_NORTHEAST_2;

    @Bean
    public LambdaClient lambdaClient() {
        return LambdaClient.builder()
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder()
                                .retryPolicy(RetryPolicy.none()) // Retry 설정 (필요에 따라 변경 가능)
                                .apiCallTimeout(Duration.ofMinutes(3)) // 전체 API 호출 타임아웃 설정
                                .apiCallAttemptTimeout(Duration.ofMinutes(3)) // 개별 호출 시도에 대한 타임아웃 설정
                                .build()
                )
                .httpClient(
                        ApacheHttpClient.builder()
                                .socketTimeout(Duration.ofMinutes(3)) // 읽기 타임아웃 설정
                                .connectionTimeout(Duration.ofSeconds(3)) // 연결 타임아웃 설정
                                .build()
                )
                .region(region)
                .build();
    }
}
