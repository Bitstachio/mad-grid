package com.github.barbodh.madgridapi.lobby;

import com.github.barbodh.madgridapi.BaseServiceTest;
import com.github.barbodh.madgridapi.exception.PlayerAlreadyInGameException;
import com.github.barbodh.madgridapi.game.model.MultiplayerGame;
import com.github.barbodh.madgridapi.game.service.GameService;
import com.github.barbodh.madgridapi.lobby.dao.LobbyDao;
import com.github.barbodh.madgridapi.lobby.model.IncomingPlayer;
import com.github.barbodh.madgridapi.lobby.service.LobbyServiceImpl;
import com.github.barbodh.madgridapi.registry.service.PlayerRegistryService;
import com.github.barbodh.madgridapi.transaction.FirestoreTransactional;
import com.github.barbodh.madgridapi.util.ArgumentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LobbyServiceImplTest extends BaseServiceTest {
    @Mock
    private LobbyDao lobbyDao;
    @Mock
    private GameService gameService;
    @Mock
    private PlayerRegistryService playerRegistryService;
    @InjectMocks
    private LobbyServiceImpl lobbyServiceImpl;

    @Test
    public void testAnnotations() throws NoSuchMethodException {
        assertTrue(LobbyServiceImpl.class.getMethod("matchPlayer", IncomingPlayer.class).isAnnotationPresent(FirestoreTransactional.class));
        assertTrue(LobbyServiceImpl.class.getMethod("removePlayer", String.class).isAnnotationPresent(FirestoreTransactional.class));
    }

    @Test
    public void testMatchPlayer_basicArgumentValidation() {
        skipUnverifiedMockInteractionCheck = true;
        try (var mockedArgumentValidator = mockStatic(ArgumentValidator.class)) {
            var incomingPlayer = new IncomingPlayer("123", 0);

            lobbyServiceImpl.matchPlayer(incomingPlayer);

            mockedArgumentValidator.verify(() -> ArgumentValidator.validatePlayerId(incomingPlayer.getId()));
            mockedArgumentValidator.verify(() -> ArgumentValidator.validateGameMode(incomingPlayer.getGameMode()));
        }
    }

    @Test
    public void testMatchPlayer_opponentFound() {
        var incomingPlayer = new IncomingPlayer("123", 0);
        var opponent = new IncomingPlayer("987", 0);
        var expectedMultiplayerGameInstance = new MultiplayerGame();
        when(gameService.create(incomingPlayer.getGameMode(), incomingPlayer.getId(), opponent.getId()))
                .thenReturn(expectedMultiplayerGameInstance);
        when(lobbyDao.findOpponent(incomingPlayer)).thenReturn(Optional.of(opponent));

        var multiplayerGame = lobbyServiceImpl.matchPlayer(incomingPlayer);

        verify(lobbyDao).findOpponent(incomingPlayer);
        verify(playerRegistryService).exists(incomingPlayer.getId());
        verify(gameService).create(incomingPlayer.getGameMode(), incomingPlayer.getId(), opponent.getId());
        verify(lobbyDao).deleteById(opponent.getId());
        assertTrue(multiplayerGame.isPresent());
        assertEquals(expectedMultiplayerGameInstance, multiplayerGame.get());
    }

    @Test
    public void testMatchPlayer_opponentNotFound() {
        var incomingPlayer = new IncomingPlayer("123", 0);
        when(lobbyDao.findOpponent(incomingPlayer)).thenReturn(Optional.empty());

        var multiplayerGame = lobbyServiceImpl.matchPlayer(incomingPlayer);

        verify(lobbyDao).findOpponent(incomingPlayer);
        verify(playerRegistryService).exists(incomingPlayer.getId());
        verify(lobbyDao).save(incomingPlayer);
        assertTrue(multiplayerGame.isEmpty());
    }

    @Test
    public void testMatchPlayer_playerAlreadyInGame() {
        var incomingPlayer = new IncomingPlayer("123", 0);
        when(playerRegistryService.exists(incomingPlayer.getId())).thenReturn(true);

        assertThrows(PlayerAlreadyInGameException.class, () -> lobbyServiceImpl.matchPlayer(incomingPlayer));
    }

    @Test
    public void testRemovePlayer_basicArgumentValidation() {
        skipUnverifiedMockInteractionCheck = true;
        try (var mockedArgumentValidator = mockStatic(ArgumentValidator.class)) {
            var playerId = "123";
            lobbyServiceImpl.removePlayer(playerId);
            mockedArgumentValidator.verify(() -> ArgumentValidator.validatePlayerId(playerId));
        }
    }

    @Test
    public void testRemovePlayer() {
        var playerId = "123";

        lobbyServiceImpl.removePlayer(playerId);

        verify(lobbyDao).deleteById(playerId);
    }
}
