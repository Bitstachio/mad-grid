package com.github.barbodh.madgridapi.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerHeartbeat {
    private String gameId;
    private String playerId;
}
