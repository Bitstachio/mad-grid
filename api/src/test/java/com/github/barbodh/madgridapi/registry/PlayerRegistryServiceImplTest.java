package com.github.barbodh.madgridapi.registry;

import com.github.barbodh.madgridapi.BaseServiceTest;
import com.github.barbodh.madgridapi.registry.dao.PlayerRegistryDao;
import com.github.barbodh.madgridapi.registry.service.PlayerRegistryServiceImpl;
import com.github.barbodh.madgridapi.transaction.FirestoreTransactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PlayerRegistryServiceImplTest extends BaseServiceTest {
    private final String playerId = "123";

    @Mock
    private PlayerRegistryDao playerRegistryDao;
    @InjectMocks
    private PlayerRegistryServiceImpl playerRegistryServiceImpl;

    @Test
    public void testAnnotations() throws NoSuchMethodException {
        assertTrue(PlayerRegistryServiceImpl.class.getMethod("update", String.class).isAnnotationPresent(FirestoreTransactional.class));
        assertTrue(PlayerRegistryServiceImpl.class.getMethod("exists", String.class).isAnnotationPresent(FirestoreTransactional.class));
        assertTrue(PlayerRegistryServiceImpl.class.getMethod("delete", String.class).isAnnotationPresent(FirestoreTransactional.class));
    }

    @Test
    public void testUpdate() {
        playerRegistryServiceImpl.update(playerId);

        verify(playerRegistryDao).update(playerId);
    }

    @Test
    public void testExists() {
        playerRegistryServiceImpl.exists(playerId);

        verify(playerRegistryDao).exists(playerId);
    }

    @Test
    public void testDelete() {
        playerRegistryServiceImpl.delete(playerId);

        verify(playerRegistryDao).delete(playerId);
    }
}
