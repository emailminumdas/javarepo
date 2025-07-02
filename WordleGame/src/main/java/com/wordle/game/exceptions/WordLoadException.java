package com.wordle.game.exceptions;

public class WordLoadException extends GameException {
    private final String fileName;

    public WordLoadException(String fileName, String message) {
        super(String.format("Failed to load words from file '%s' : %s", fileName, message));
        this.fileName = fileName;
    }

    public WordLoadException(String fileName, String message, Throwable cause) {
        super(String.format("Failed to load words from file '%s' : %s", fileName, message), cause);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
