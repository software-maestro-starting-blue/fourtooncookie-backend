package com.startingblue.fourtooncookie.aws.lambda;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.core.SdkBytes;

@Component
public class LambdaInvoker {

    public static void invokeLambda(LambdaClient lambdaClient, String functionName, String payload) {
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(functionName)
                .payload(SdkBytes.fromUtf8String(payload))
                .build();

        lambdaClient.invoke(invokeRequest);
    }
}
