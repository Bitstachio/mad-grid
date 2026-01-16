//package com.github.barbodh.madgridapi.integration;
//
//import com.github.barbodh.madgridapi.game.model.GameUpdate;
//import com.github.barbodh.madgridapi.game.model.MultiplayerGame;
//import com.github.barbodh.madgridapi.lobby.model.IncomingPlayer;
//import com.github.barbodh.madgridapi.lobby.model.LobbyNotification;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//
//import java.lang.reflect.Type;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
//public class PublishToServerTest {
//    @LocalServerPort
//    private int port;
//    private final MappingJackson2MessageConverter converter;
//
//    @Autowired
//    public PublishToServerTest(MappingJackson2MessageConverter converter) {
//        this.converter = converter;
//    }
//
//    @Test
//    public void testPublishPlayerToLobby() throws InterruptedException {
//        var clientPayload = new IncomingPlayer("123", 0);
//        var timeout = 3; // Seconds
//        var stompClient = new WebSocketStompClient(new StandardWebSocketClient());
//        var sessionHandler = new ClientStompSessionHandler(new CountDownLatch(2)) {
//            @Override
//            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                session.subscribe("/player/" + clientPayload.getId() + "/lobby/notify", this);
//                session.send("/game/seek-opponent", clientPayload);
//                getLatch().countDown();
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                getLatch().countDown();
//            }
//
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return LobbyNotification.class;
//            }
//        };
//
//        stompClient.setMessageConverter(converter);
//        stompClient.setTaskScheduler(new DefaultManagedTaskScheduler());
//        stompClient.connectAsync(String.format("ws://localhost:%d/ws", port), sessionHandler);
//
//        var messageSent = sessionHandler.getLatch().await(timeout, TimeUnit.SECONDS);
//        assertTrue(messageSent);
//    }
//
//    @Test
//    public void testPublishGameUpdate() throws InterruptedException {
//        var clientPayload = new GameUpdate("127_126", "127", true);
//        var timeout = 3; // Seconds
//        var stompClient = new WebSocketStompClient(new StandardWebSocketClient());
//        var sessionHandler = new ClientStompSessionHandler(new CountDownLatch(2)) {
//            @Override
//            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                session.subscribe("/player/" + clientPayload.getPlayerId() + "/game/notify", this);
//                session.send("/game/update", clientPayload);
//                getLatch().countDown();
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                getLatch().countDown();
//            }
//
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return MultiplayerGame.class;
//            }
//        };
//
//        stompClient.setMessageConverter(converter);
//        stompClient.setTaskScheduler(new DefaultManagedTaskScheduler());
//        stompClient.connectAsync(String.format("ws://localhost:%d/ws", port), sessionHandler);
//
//        var messageSent = sessionHandler.getLatch().await(timeout, TimeUnit.SECONDS);
//        assertTrue(messageSent);
//    }
//}
