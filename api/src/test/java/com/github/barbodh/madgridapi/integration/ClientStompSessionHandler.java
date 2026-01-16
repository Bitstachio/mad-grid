package com.github.barbodh.madgridapi.integration;

import lombok.Getter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;

@Getter
public class ClientStompSessionHandler implements StompSessionHandler {
    private final CountDownLatch latch;

    public ClientStompSessionHandler(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println("\n\n* handleException");
        exception.printStackTrace();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.out.println("\n\n* handleTransportError");
        exception.printStackTrace();
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Object.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {

    }
}
