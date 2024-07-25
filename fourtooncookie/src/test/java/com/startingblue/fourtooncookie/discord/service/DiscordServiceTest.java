package com.startingblue.fourtooncookie.discord.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiscordServiceTest {

    @Autowired
    DiscordService discordService;

    @Value("${discord.test.guildid}")
    private Long testGuildId;

    @Value("${discord.test.channelid}")
    private Long testChannelId;

    @Test
    @DisplayName("디스코드 서비스에서 메시지 정상적으로 전달되는지를 테스트")
    void sendMessage() {
        // given
        Long guildId = testGuildId;
        Long channelId = testChannelId;
        String message = "테스트 메시지입니다.";

        // when
        discordService.sendMessage(guildId, channelId, message).join();

        // then
        // 특별한 검증이 필요 없음
    }


}