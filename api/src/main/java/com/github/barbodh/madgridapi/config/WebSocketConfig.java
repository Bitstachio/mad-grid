package com.github.barbodh.madgridapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/game");
        registry.enableSimpleBroker("/player");
        registry.setUserDestinationPrefix("/player");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws").withSockJS();
        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        var resolver = new DefaultContentTypeResolver();
        var converter = new MappingJackson2MessageConverter();

        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);

        return false;
    }
}
