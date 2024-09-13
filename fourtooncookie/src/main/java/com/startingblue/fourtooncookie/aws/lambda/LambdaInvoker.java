package com.startingblue.fourtooncookie.aws.lambda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

@RequiredArgsConstructor
@Slf4j
public abstract class LambdaInvoker {

    private final LambdaClient lambdaClient;

    public void invokeLambdaAsync(Object payload) {
        try {
            String serializedPayload = serializePayload(payload);
            invokeLambda(serializedPayload);
        } catch (Exception e) {
            log.error("Lambda 호출 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("Lambda 호출 실패", e);
        }
    }

    protected abstract String serializePayload(Object payload);

    private void invokeLambda(String payload) {
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(getFunctionName())
                .payload(SdkBytes.fromUtf8String(payload))
                .invocationType(InvocationType.REQUEST_RESPONSE)
                .build();

        var response = lambdaClient.invoke(invokeRequest);
        if (response.payload().asUtf8String().equals("false")) {
            throw new RuntimeException("Lambda 호출 실패");
        }
    }

    protected abstract String getFunctionName();
}
