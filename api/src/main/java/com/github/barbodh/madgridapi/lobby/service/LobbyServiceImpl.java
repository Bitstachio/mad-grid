package com.github.barbodh.madgridapi.lobby.service;

import com.github.barbodh.madgridapi.exception.PlayerAlreadyInGameException;
import com.github.barbodh.madgridapi.game.model.MultiplayerGame;
import com.github.barbodh.madgridapi.game.service.GameService;
import com.github.barbodh.madgridapi.lobby.dao.LobbyDao;
import com.github.barbodh.madgridapi.lobby.model.IncomingPlayer;
import com.github.barbodh.madgridapi.registry.service.PlayerRegistryService;
import com.github.barbodh.madgridapi.transaction.FirestoreTransactional;
import com.github.barbodh.madgridapi.util.ArgumentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LobbyServiceImpl implements LobbyService {
    private final GameService gameService;
    private final LobbyDao lobbyDao;
    private final PlayerRegistryService playerRegistryService;

    @FirestoreTransactional
    @Override
    public Optional<MultiplayerGame> matchPlayer(IncomingPlayer incomingPlayer) {
        ArgumentValidator.validatePlayerId(incomingPlayer.getId());
        ArgumentValidator.validateGameMode(incomingPlayer.getGameMode());

        if (playerRegistryService.exists(incomingPlayer.getId())) {
            throw new PlayerAlreadyInGameException();
        }

        return lobbyDao.findOpponent(incomingPlayer)
                .map(opponent -> {
                    lobbyDao.deleteById(opponent.getId());
                    return gameService.create(incomingPlayer.getGameMode(), incomingPlayer.getId(), opponent.getId());
                })
                .or(() -> {
                    lobbyDao.save(incomingPlayer);
                    return Optional.empty();
                });
    }

    @FirestoreTransactional
    @Override
    public void removePlayer(String playerId) {
        ArgumentValidator.validatePlayerId(playerId);
        lobbyDao.deleteById(playerId);
    }
}
