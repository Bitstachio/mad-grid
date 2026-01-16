package com.github.barbodh.madgridapi.integration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncomingPlayerClientPayload {
    private String id;
    private int gameMode;
}
