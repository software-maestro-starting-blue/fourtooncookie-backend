package com.startingblue.fourtooncookie.aws.lambda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.core.SdkBytes;

@Service
@RequiredArgsConstructor
@Slf4j
public class LambdaInvoker {

    private final LambdaClient lambdaClient;

    public void invokeLambda(String functionName, String payload) {
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(functionName)
                .payload(SdkBytes.fromUtf8String(payload))
                .invocationType(InvocationType.REQUEST_RESPONSE)
                .build();

        var response = lambdaClient.invoke(invokeRequest);
        if (response.payload().asUtf8String().equals("false")) {
            throw new RuntimeException("Lambda invocation failed");
        }
    }

}
