package com.github.barbodh.madgridapi.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArgumentValidatorTest {
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void testValidateGameMode_valid(int gameMode) {
        ArgumentValidator.validateGameMode(gameMode);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 3})
    public void testValidateGameMode_invalid(int gameMode) {
        assertThrows(IllegalArgumentException.class, () -> ArgumentValidator.validateGameMode(gameMode));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "goodString", "good123str", "1234567"})
    public void testValidatePlayerId_valid(String playerId) {
        ArgumentValidator.validatePlayerId(playerId);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "this_is_a_very_long_string"})
    public void testValidatePlayerId_invalid(String playerId) {
        assertThrows(IllegalArgumentException.class, () -> ArgumentValidator.validatePlayerId(playerId));
    }
}
