package com.github.barbodh.madgridapi.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class StringUtilTest {
    private final String playerId1 = "123";
    private final String playerId2 = "987";

    @Test
    public void testGenerateGameId_exceptionHandling() {
        try (var mockedArgumentValidator = mockStatic(ArgumentValidator.class)) {
            StringUtil.generateGameId(playerId1, playerId2);

            mockedArgumentValidator.verify(() -> ArgumentValidator.validatePlayerId(playerId1));
            mockedArgumentValidator.verify(() -> ArgumentValidator.validatePlayerId(playerId2));
        }
    }

    @Test
    public void testGenerateGameId() {
        assertEquals("123_987", StringUtil.generateGameId(playerId1, playerId2));
    }
}
