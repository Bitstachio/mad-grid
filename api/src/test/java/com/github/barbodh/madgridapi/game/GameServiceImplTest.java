package com.github.barbodh.madgridapi.game;

import com.github.barbodh.madgridapi.BaseServiceTest;
import com.github.barbodh.madgridapi.exception.InvalidGameStateException;
import com.github.barbodh.madgridapi.exception.ScoreUpdateNotAllowedException;
import com.github.barbodh.madgridapi.game.dao.GameDao;
import com.github.barbodh.madgridapi.game.model.GameUpdate;
import com.github.barbodh.madgridapi.game.model.MultiplayerGame;
import com.github.barbodh.madgridapi.game.model.Player;
import com.github.barbodh.madgridapi.game.service.GameServiceImpl;
import com.github.barbodh.madgridapi.registry.service.PlayerRegistryService;
import com.github.barbodh.madgridapi.transaction.FirestoreTransactional;
import com.github.barbodh.madgridapi.util.ArgumentValidator;
import com.github.barbodh.madgridapi.util.StringUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest extends BaseServiceTest {
    @Mock
    private GameDao gameDao;
    @Mock
    private PlayerRegistryService playerRegistryService;
    @InjectMocks
    private GameServiceImpl gameServiceImpl;

    @Test
    public void testAnnotations() throws NoSuchMethodException {
        assertTrue(GameServiceImpl.class.getMethod("create", int.class, String.class, String.class).isAnnotationPresent(FirestoreTransactional.class));
        assertTrue(GameServiceImpl.class.getMethod("update", GameUpdate.class).isAnnotationPresent(FirestoreTransactional.class));
    }

    @Test
    public void testCreateMultiplayerGame_basicArgumentValidation() {
        skipUnverifiedMockInteractionCheck = true;
        try (var mockedArgumentValidator = mockStatic(ArgumentValidator.class)) {
            var gameMode = 2;
            var playerId1 = "31028";
            var playerId2 = "60682";

            gameServiceImpl.create(gameMode, playerId1, playerId2);

            mockedArgumentValidator.verify(() -> ArgumentValidator.validateGameMode(gameMode));
            mockedArgumentValidator.verify(() -> ArgumentValidator.validatePlayerId(playerId1));
            mockedArgumentValidator.verify(() -> ArgumentValidator.validatePlayerId(playerId2));
        }
    }

    @Test
    public void testCreateMultiplayerGame() {
        var gameMode = 2;
        var playerId1 = "31028";
        var playerId2 = "60682";

        var multiplayerGame = gameServiceImpl.create(gameMode, playerId1, playerId2);

        verify(gameDao).save(multiplayerGame);
        verify(playerRegistryService).update(multiplayerGame.getPlayer1().getId());
        verify(playerRegistryService).update(multiplayerGame.getPlayer2().getId());
        assertEquals(StringUtil.generateGameId(playerId1, playerId2), multiplayerGame.getId());
        assertEquals(gameMode, multiplayerGame.getGameMode());
        assertEquals(playerId1, multiplayerGame.getPlayer1().getId());
        assertEquals(playerId2, multiplayerGame.getPlayer2().getId());
        assertEquals(0, multiplayerGame.getPlayer1().getScore());
        assertEquals(0, multiplayerGame.getPlayer2().getScore());
        assertTrue(multiplayerGame.isActive());
        assertTrue(multiplayerGame.getPlayer1().isPlaying());
        assertTrue(multiplayerGame.getPlayer2().isPlaying());
    }

    private static Stream<Arguments> provideArgs_testUpdateGame() {
        return Stream.of(
                Arguments.of(true, 6, 5, true, false),
                Arguments.of(true, 5, 5, true, false),
                Arguments.of(true, 5, 5, false, true),
                Arguments.of(true, 4, 5, true, false),
                Arguments.of(true, 4, 5, false, false),
                Arguments.of(true, 3, 5, true, false),
                Arguments.of(true, 3, 5, false, false),
                Arguments.of(false, 6, 5, true, false),
                Arguments.of(false, 5, 5, true, false),
                Arguments.of(false, 5, 5, false, true),
                Arguments.of(false, 4, 5, true, true),
                Arguments.of(false, 4, 5, false, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgs_testUpdateGame")
    public void testUpdateGame(boolean result, int score1, int score2, boolean opponentActivity, boolean finishGame) {
        var player1 = new Player("123", score1, true);
        var player2 = new Player("987", score2, opponentActivity);
        var game = new MultiplayerGame(StringUtil.generateGameId(player1.getId(), player2.getId()), 0, player1, player2, true);
        var gameUpdate = new GameUpdate(game.getId(), player1.getId(), result);
        when(gameDao.findById(game.getId())).thenReturn(Optional.of(new MultiplayerGame(game.getId(), game.getGameMode(), player1, player2, true)));

        var updatedGame = gameServiceImpl.update(gameUpdate);
        player1.incrementScore();

        if (finishGame) {
            game.finish();

            verify(playerRegistryService).delete(game.getPlayer1().getId());
            verify(playerRegistryService).delete(game.getPlayer2().getId());
            verify(gameDao).deleteById(game.getId());
            assertEquals(game, updatedGame);
        } else {
            verify(gameDao).save(updatedGame);
            assertEquals(game, updatedGame);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testUpdateGame_invalidGameState(boolean result) {
        var player1 = new Player("123", 6, true);
        var player2 = new Player("987", 5, false);
        var game = new MultiplayerGame(StringUtil.generateGameId(player1.getId(), player2.getId()), 0, player1, player2, true);
        var gameUpdate = new GameUpdate(game.getId(), player1.getId(), result);
        when(gameDao.findById(game.getId())).thenReturn(Optional.of(new MultiplayerGame(game.getId(), game.getGameMode(), player1, player2, true)));

        assertThrows(InvalidGameStateException.class, () -> gameServiceImpl.update(gameUpdate));
    }

    @Test
    public void testUpdateGame_updateFinishedPlayer() {
        var finishedPlayer = new Player("123", 9, false);
        var player = new Player("987", 8, true);
        var game = new MultiplayerGame(StringUtil.generateGameId(finishedPlayer.getId(), player.getId()), 0, finishedPlayer, player, true);
        var gameUpdate = new GameUpdate(game.getId(), finishedPlayer.getId(), false);
        when(gameDao.findById(game.getId())).thenReturn(Optional.of(new MultiplayerGame(game.getId(), game.getGameMode(), finishedPlayer, player, true)));

        assertThrows(ScoreUpdateNotAllowedException.class, () -> gameServiceImpl.update(gameUpdate));
    }

    @Test
    public void testUpdateGame_invalidGameId() {
        var player1 = new Player("123", 9, true);
        var player2 = new Player("987", 8, true);
        var game = new MultiplayerGame(StringUtil.generateGameId(player1.getId(), player2.getId()), 0, player1, player2, true);
        var gameUpdate = new GameUpdate(game.getId(), player2.getId(), true);
        when(gameDao.findById(game.getId())).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () -> gameServiceImpl.update(gameUpdate));
        assertTrue(exception.getMessage().contains(game.getId()));
    }

    @Test
    public void testUpdateGame_invalidPlayerId() {
        var player1 = new Player("123", 8, true);
        var player2 = new Player("987", 9, true);
        var player3 = new Player("456", 9, true);
        var game = new MultiplayerGame(StringUtil.generateGameId(player1.getId(), player2.getId()), 0, player1, player2, true);
        var gameUpdate = new GameUpdate(game.getId(), player3.getId(), true);
        when(gameDao.findById(game.getId())).thenReturn(Optional.of(new MultiplayerGame(game.getId(), game.getGameMode(), player1, player2, true)));

        var exception = assertThrows(IllegalArgumentException.class, () -> gameServiceImpl.update(gameUpdate));
        assertTrue(exception.getMessage().contains(gameUpdate.getPlayerId()));
    }

}
