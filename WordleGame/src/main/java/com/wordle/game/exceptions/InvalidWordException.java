package com.wordle.game.exceptions;

public class InvalidWordException extends GameException {
    private final String invalidWord;

    public InvalidWordException(String invalidWord, String message) {
        super(String.format("Invalid word '%s': %s", invalidWord, message));
        this.invalidWord = invalidWord;
    }

    public InvalidWordException(String invalidWord, String message, Throwable cause) {
        super(String.format("Invalid word '%s': %s", invalidWord, message), cause);
        this.invalidWord = invalidWord;
    }

    public String getInvalidWord() {
        return invalidWord;
    }
}

