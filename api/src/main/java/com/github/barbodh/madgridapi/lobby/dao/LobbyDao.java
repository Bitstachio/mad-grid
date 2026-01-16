package com.github.barbodh.madgridapi.lobby.dao;

import com.github.barbodh.madgridapi.lobby.model.IncomingPlayer;

import java.util.Optional;

public interface LobbyDao {
    void save(IncomingPlayer incomingPlayer);

    Optional<IncomingPlayer> findOpponent(IncomingPlayer incomingPlayer);

    void deleteById(String id);
}
