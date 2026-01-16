package com.github.barbodh.madgridapi.game.service;

import com.github.barbodh.madgridapi.exception.InvalidGameStateException;
import com.github.barbodh.madgridapi.exception.ScoreUpdateNotAllowedException;
import com.github.barbodh.madgridapi.game.dao.GameDao;
import com.github.barbodh.madgridapi.game.model.GameUpdate;
import com.github.barbodh.madgridapi.game.model.MultiplayerGame;
import com.github.barbodh.madgridapi.game.model.Player;
import com.github.barbodh.madgridapi.registry.service.PlayerRegistryService;
import com.github.barbodh.madgridapi.transaction.FirestoreTransactional;
import com.github.barbodh.madgridapi.util.ArgumentValidator;
import com.github.barbodh.madgridapi.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameDao gameDao;
    private final PlayerRegistryService playerRegistryService;

    @FirestoreTransactional
    @Override
    public MultiplayerGame create(int gameMode, String playerId1, String playerId2) {
        ArgumentValidator.validateGameMode(gameMode);

        var game = new MultiplayerGame(
                StringUtil.generateGameId(playerId1, playerId2),
                gameMode,
                new Player(playerId1),
                new Player(playerId2)
        );
        gameDao.save(game);
        playerRegistryService.update(playerId1);
        playerRegistryService.update(playerId2);

        return game;
    }

    @FirestoreTransactional
    @Override
    public MultiplayerGame update(GameUpdate gameUpdate) {
        return gameDao.findById(gameUpdate.getGameId())
                .map(game -> {
                    var player1 = game.getPlayer1();
                    var player2 = game.getPlayer2();

                    if (player1.getId().equals(player2.getId())) {
                        throw new IllegalArgumentException(String.format("Players cannot have identical IDs.\nProvided players:\n%s\n%s", player1, player2));
                    }
                    if (!gameUpdate.getPlayerId().equals(player1.getId()) && !gameUpdate.getPlayerId().equals(player2.getId())) {
                        throw new IllegalArgumentException("Player not found. Provided ID: " + gameUpdate.getPlayerId());
                    }

                    var player = gameUpdate.getPlayerId().equals(player1.getId()) ? player1 : player2;
                    var other = player.equals(player1) ? player2 : player1;

                    if (!player.isPlaying()) {
                        throw new ScoreUpdateNotAllowedException();
                    }
                    if (player.getScore() > other.getScore() && !other.isPlaying()) {
                        throw new InvalidGameStateException();
                    }

                    if (gameUpdate.isResult()) {
                        player.incrementScore();
                        if ((player.getScore() > player1.getScore() || player.getScore() > player2.getScore()) // Player is leading
                                && (!player1.isPlaying() || !player2.isPlaying())) { // AND other player (trailing) is inactive
                            return finishGame(game, gameUpdate);
                        }
                        if (Math.abs(player1.getScore() - player2.getScore()) == 4
                                && (player.getScore() > player1.getScore() || player.getScore() > player2.getScore())) {
                            return finishGame(game, gameUpdate);
                        }
                    } else {
                        player.setPlaying(false);
                        if ((player.getScore() < player1.getScore() || player.getScore() < player2.getScore()) // Player is trailing
                                || (!player1.isPlaying() && !player2.isPlaying())) { // OR neither player is active
                            return finishGame(game, gameUpdate);
                        }
                    }

                    gameDao.save(game);
                    return game;
                })
                .orElseThrow(() -> new IllegalArgumentException("Game not found. Provided ID: " + gameUpdate.getGameId()));
    }

    private MultiplayerGame finishGame(MultiplayerGame multiplayerGame, GameUpdate gameUpdate) {
        multiplayerGame.finish();
        gameDao.deleteById(gameUpdate.getGameId());
        playerRegistryService.delete(multiplayerGame.getPlayer1().getId());
        playerRegistryService.delete(multiplayerGame.getPlayer2().getId());
        return multiplayerGame;
    }
}
