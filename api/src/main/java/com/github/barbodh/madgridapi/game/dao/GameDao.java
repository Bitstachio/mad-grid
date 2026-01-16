package com.github.barbodh.madgridapi.game.dao;

import com.github.barbodh.madgridapi.game.model.MultiplayerGame;

import java.util.Optional;

public interface GameDao {
    void save(MultiplayerGame multiplayerGame);

    Optional<MultiplayerGame> findById(String id);

    void deleteById(String id);
}
