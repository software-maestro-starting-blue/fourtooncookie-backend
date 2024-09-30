package com.startingblue.fourtooncookie.aws.lambda.service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

@Slf4j
public abstract class LambdaService {

    private final LambdaClient lambdaClient;
    private final String functionName;
    private final InvocationType invocationType;

    public LambdaService(LambdaClient lambdaClient, String functionName, InvocationType invocationType) {
        this.lambdaClient = lambdaClient;
        this.functionName = functionName;
        this.invocationType = invocationType;
    }

    public void invokeLambda(Object payload) {
        try {
            log.info("Invoking Lambda function: {} with payload: {}", functionName);

            InvokeRequest invokeRequest = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(SdkBytes.fromUtf8String(payload.toString()))
                    .invocationType(invocationType)
                    .build();

            var response = lambdaClient.invoke(invokeRequest);

            if (response.payload().asUtf8String().equals("false")) {
                log.error("Lambda 호출 중 오류 발생: {}", functionName);
                throw new RuntimeException("Lambda 호출 실패");
            }

            log.info("Lambda 호출 성공: {}", functionName);
        } catch (Exception e) {
            log.error("Lambda 호출 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Lambda 호출 실패", e);
        }
    }
}
