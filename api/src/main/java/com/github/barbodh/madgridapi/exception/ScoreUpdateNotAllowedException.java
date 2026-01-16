package com.github.barbodh.madgridapi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ScoreUpdateNotAllowedException extends RuntimeException{
    public ScoreUpdateNotAllowedException(String message) {
        super(message);
    }
}
