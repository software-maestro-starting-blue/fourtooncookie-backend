package com.startingblue.fourtooncookie.global.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        return new XmlMessageSource();
    }
}
