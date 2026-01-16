package com.github.barbodh.madgridapi.util;

public class StringUtil {
    public static String generateGameId(String playerId1, String playerId2) {
        ArgumentValidator.validatePlayerId(playerId1);
        ArgumentValidator.validatePlayerId(playerId2);

        return String.format("%s_%s", playerId1, playerId2);
    }
}
