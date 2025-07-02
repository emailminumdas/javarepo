package com.wordle.game.logic;

public enum GuessOutcome {
    CORRECT("Correct Position"), WRONG_POSITION("Wrong Position"), INCORRECT("Not in the word");
    private final String description;

    GuessOutcome(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "GuessOutcome{" + "description='" + description + '\'' + '}';
    }
}
