package com.github.barbodh.madgridapi.lobby.controller;

import com.github.barbodh.madgridapi.game.heartbeat.HeartbeatManager;
import com.github.barbodh.madgridapi.lobby.model.IncomingPlayer;
import com.github.barbodh.madgridapi.lobby.model.LobbyNotification;
import com.github.barbodh.madgridapi.lobby.service.LobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LobbyController {
    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;
    private final HeartbeatManager heartbeatManager;

    @MessageMapping("/seek-opponent")
    public void handleIncomingUser(@Payload IncomingPlayer incomingPlayer) {
        final String notificationUrl = "/lobby/notify";
        lobbyService.matchPlayer(incomingPlayer).ifPresentOrElse(
                multiplayerGame -> {
                    heartbeatManager.update(multiplayerGame.getId(), multiplayerGame.getPlayer1().getId());
                    heartbeatManager.update(multiplayerGame.getId(), multiplayerGame.getPlayer2().getId());

                    messagingTemplate.convertAndSendToUser(
                            multiplayerGame.getPlayer1().getId(),
                            notificationUrl,
                            new LobbyNotification(multiplayerGame)
                    );
                    messagingTemplate.convertAndSendToUser(
                            multiplayerGame.getPlayer2().getId(),
                            notificationUrl,
                            new LobbyNotification(multiplayerGame)
                    );
                },
                () -> messagingTemplate.convertAndSendToUser(
                        incomingPlayer.getId(),
                        notificationUrl,
                        new LobbyNotification()
                )
        );
    }

    @MessageMapping("/exit-lobby")
    public void exitLobby(String playerId) {
        lobbyService.removePlayer(playerId);
    }
}
