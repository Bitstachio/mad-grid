package com.github.barbodh.madgridapi.util;

public class ArgumentValidator {
    public static void validateGameMode(int gameMode) {
        if (gameMode < 0 || gameMode > 2) {
            throw new IllegalArgumentException("Game mode should be within the range of 0 to 2. Provided: " + gameMode);
        }
    }

    public static void validatePlayerId(String playerId) {
        if (playerId == null || playerId.isEmpty()) {
            throw new IllegalArgumentException("Player ID should not be empty. Provided: " + playerId);
        }
        if (playerId.length() > 10) {
            throw new IllegalArgumentException("Player ID should not exceed 10 characters. Provided: " + playerId);
        }
    }
}
