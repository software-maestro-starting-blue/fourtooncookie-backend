package com.startingblue.fourtooncookie.aws.lambda;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.core.SdkBytes;

@Service
@RequiredArgsConstructor
public class LambdaInvoker {

    private final LambdaClient lambdaClient;

    public void invokeLambda(String functionName, String payload) {
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(functionName)
                .payload(SdkBytes.fromUtf8String(payload))
                .invocationType(InvocationType.EVENT)
                .build();

        lambdaClient.invoke(invokeRequest);
    }
}
