package com.startingblue.fourtooncookie.vision.apply.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DallE3VisionApplyServiceTest {

    @Autowired
    DallE3VisionApplyService service;

    @Test
    @DisplayName("Dall-E 3 통신되는가를 테스트")
    void Test1() throws Exception {
        Class<?> clazz = DallE3VisionApplyService.class;

        Method getImageFromDallE3 = clazz.getDeclaredMethod("getImageFromDallE3", String.class);

        getImageFromDallE3.setAccessible(true);

        // Invoke the method on the service instance
        String result = (String) getImageFromDallE3.invoke(service, "Rainbow");

        // Assert that the result is not null (or any other assertion as needed)
        assertNotNull(result);
    }
}