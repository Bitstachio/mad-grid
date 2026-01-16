package com.github.barbodh.madgridapi.game.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MultiplayerGame {
    private String id;
    private int gameMode;
    private Player player1;
    private Player player2;
    private boolean active;

    public MultiplayerGame(String id, int gameMode, Player player1, Player player2) {
        this.id = id;
        this.gameMode = gameMode;
        this.player1 = player1;
        this.player2 = player2;
        this.active = true;
    }

    public void finish() {
        active = false;
        player1.setPlaying(false);
        player2.setPlaying(false);
    }
}
