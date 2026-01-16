package com.github.barbodh.madgridapi.game.service;

import com.github.barbodh.madgridapi.game.model.GameUpdate;
import com.github.barbodh.madgridapi.game.model.MultiplayerGame;

public interface GameService {
    MultiplayerGame create(int gameMode, String playerId1, String playerId2);

    MultiplayerGame update(GameUpdate gameUpdate);
}
