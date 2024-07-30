package com.startingblue.fourtooncookie.aws.sqs.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.sqs.model.Message;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StableDiffusionSQSReplyServiceTest {

    @Autowired
    StableDiffusionSQSReplyService stableDiffusionSQSReplyService;

    //@Test
    @DisplayName("getMessagesFromSQS 실제 테스트")
    void getMessageFromSQSTest() throws Exception {
        // given
        Method method = StableDiffusionSQSReplyService.class.getDeclaredMethod("getMessagesFromSQS");
        method.setAccessible(true);

        // when
        List<Message> messages = (List<Message>) method.invoke(stableDiffusionSQSReplyService);

        // then
        assertNotNull(messages);

        System.out.println("messages.get(0).body() = " + messages.get(0).body());
    }

}