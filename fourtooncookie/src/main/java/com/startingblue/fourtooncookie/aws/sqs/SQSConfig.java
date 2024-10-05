package com.startingblue.fourtooncookie.aws.sqs;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.AcknowledgementOrdering;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.Duration;

@Configuration
public class SQSConfig {

    private static final Region REGION = Region.AP_NORTHEAST_2;
    private static final Duration ACKNOWLEDGEMENT_INTERVAL = Duration.ofSeconds(5);
    private static final int ACKNOWLEDGEMENT_THRESHOLD = 4;

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .region(REGION)
                .build();
    }

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory() {
        return SqsMessageListenerContainerFactory
                .builder()
                .configure(options -> options
                        .acknowledgementMode(AcknowledgementMode.ON_SUCCESS)
                        .acknowledgementInterval(ACKNOWLEDGEMENT_INTERVAL)
                        .acknowledgementThreshold(ACKNOWLEDGEMENT_THRESHOLD)
                        .acknowledgementOrdering(AcknowledgementOrdering.ORDERED)
                )
                .sqsAsyncClient(sqsAsyncClient())
                .build();
    }
}
