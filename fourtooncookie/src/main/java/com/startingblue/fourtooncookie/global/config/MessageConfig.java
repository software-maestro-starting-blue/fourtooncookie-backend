package com.startingblue.fourtooncookie.global.config;

import com.startingblue.fourtooncookie.global.domain.XmlMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource xmlMessageSource() {
        return new XmlMessageSource();
    }
}
