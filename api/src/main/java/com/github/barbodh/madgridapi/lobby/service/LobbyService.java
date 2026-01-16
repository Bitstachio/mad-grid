package com.github.barbodh.madgridapi.lobby.service;

import com.github.barbodh.madgridapi.game.model.MultiplayerGame;
import com.github.barbodh.madgridapi.lobby.model.IncomingPlayer;

import java.util.Optional;

public interface LobbyService {
    Optional<MultiplayerGame> matchPlayer(IncomingPlayer incomingPlayer);

    void removePlayer(String playerId);
}
