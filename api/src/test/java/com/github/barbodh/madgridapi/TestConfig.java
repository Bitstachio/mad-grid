package com.github.barbodh.madgridapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@Configuration
public class TestConfig {
    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        return new MappingJackson2MessageConverter();
    }
}
