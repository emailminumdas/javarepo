package com.wordle.game.logic;

import com.wordle.game.exceptions.GameException;
import com.wordle.game.exceptions.InvalidWordException;
import com.wordle.game.exceptions.WordLoadException;
import com.wordle.game.util.WordLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameProcessor {
    private static final int MAX_ATTEMPTS = 5;
    private static final int WORD_LENGTH = 5;

    private final WordChecker wordChecker;
    private final WordLoader wordLoader;
    private final String targetWord;
    private final List<String> guesses;
    private final List<GuessOutcome[]> guessOutcomes;

    private boolean gameWon;
    private boolean gameOver;
    private int currentAttempt;

    public GameProcessor() throws WordLoadException {
        this.wordChecker = new WordChecker();
        this.wordLoader = new WordLoader();
        this.targetWord = wordLoader.getRandomWord();
        this.guesses = new ArrayList<>();
        this.guessOutcomes = new ArrayList<>();
        this.gameWon = false;
        this.gameOver = false;
        this.currentAttempt = 0;
    }

    public GameProcessor(String targetWord) throws WordLoadException, InvalidWordException {
        this.wordChecker = new WordChecker();
        this.wordLoader = new WordLoader();

        if (!WordLoader.isWordValid(targetWord)) {
            throw new InvalidWordException(targetWord, "Target word must be exactly 5 letters");
        }

        this.targetWord = targetWord.toUpperCase();
        this.guesses = new ArrayList<>();
        this.guessOutcomes = new ArrayList<>();
        this.gameWon = false;
        this.gameOver = false;
        this.currentAttempt = 0;
    }

    public GuessOutcome[] makeGuess(String guess) throws GameException {
        if (gameOver) {
            throw new GameException("Game is already over");
        }
        if (currentAttempt >= MAX_ATTEMPTS) {
            throw new GameException("Maximum attempts exceeded");
        }

        // Validate the guess format
        if (!WordLoader.isWordValid(guess)) {
            throw new InvalidWordException(guess, "Must be exactly 5 letters containing only alphabetic characters");
        }

        String updatedGuess = guess.toUpperCase();

        // Validate against the target word
        GuessOutcome[] guessOutcomes1 = wordChecker.validateGuess(updatedGuess, targetWord);

        // Update game state
        guesses.add(updatedGuess);
        guessOutcomes.add(guessOutcomes1);
        currentAttempt++;

        // Check for win condition
        if (wordChecker.checkForExactMatch(updatedGuess, targetWord)) {
            gameWon = true;
            gameOver = true;
        } else if (currentAttempt >= MAX_ATTEMPTS) {
            gameOver = true;
        }

        return guessOutcomes1;
    }

    public boolean isGuessInWordList(String guess) {
        return wordLoader.isWordInList(guess);
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getCurrentAttempt() {
        return currentAttempt;
    }

    public int getRemainingAttempts() {
        return MAX_ATTEMPTS - currentAttempt;
    }

    public int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }

    public String getTargetWord() {
        return targetWord;
    }

    public int getWordCount() {
        return wordLoader.getWordCount();
    }

    public List<String> getGuesses() {
        return Collections.unmodifiableList(guesses);
    }

    public List<GuessOutcome[]> guessOutcomes() {
        return Collections.unmodifiableList(guessOutcomes);
    }

    public GuessOutcome[] getGuessOutcome(int attemptNumber) {
        if (attemptNumber < 0 || attemptNumber >= guessOutcomes.size()) {
            throw new IndexOutOfBoundsException("Invalid attempt number: " + attemptNumber);
        }
        return guessOutcomes.get(attemptNumber).clone();
    }

    public String getGuess(int attemptNumber) {
        if (attemptNumber < 0 || attemptNumber >= guesses.size()) {
            throw new IndexOutOfBoundsException("Invalid attempt number: " + attemptNumber);
        }
        return guesses.get(attemptNumber);
    }

    public void resetGame() throws WordLoadException {
        guesses.clear();
        guessOutcomes.clear();
        gameWon = false;
        gameOver = false;
        currentAttempt = 0;
    }

    public String getGameSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Game Status: %s%n",
                gameWon ? "WON" : gameOver ? "LOST" : "IN PROGRESS"));
        summary.append(String.format("Attempts: %d/%d%n", currentAttempt, MAX_ATTEMPTS));
        summary.append(String.format("Remaining: %d%n", getRemainingAttempts()));

        if (gameOver) {
            summary.append(String.format("Target Word: %s%n", targetWord));
        }

        return summary.toString();
    }
}
