package com.github.barbodh.madgridapi.game.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Player {
    private String id;
    private int score;
    private boolean playing;

    public Player(String id) {
        this.id = id;
        this.playing = true;
    }

    public void incrementScore() {
        score++;
    }
}
