package com.wordle.game.logic;

import com.wordle.game.exceptions.GameException;
import com.wordle.game.exceptions.InvalidWordException;
import com.wordle.game.exceptions.WordLoadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class GameProcessorTest{
    private GameProcessor gameProcessor;
    private static final String TEST_TARGET_WORD = "WATER";

    @BeforeEach
    void setUp() throws WordLoadException, InvalidWordException {
        gameProcessor = new GameProcessor(TEST_TARGET_WORD);
    }

    @Test
    @DisplayName("Initialize Game with correct values")
    void testInitialGameState(){
        assertFalse(gameProcessor.isGameWon());
        assertFalse(gameProcessor.isGameOver());
        assertEquals(0, gameProcessor.getCurrentAttempt());
        assertEquals(5, gameProcessor.getRemainingAttempts());
        assertEquals(5, gameProcessor.getMaxAttempts());
        assertTrue(gameProcessor.getGuesses().isEmpty());
        assertTrue(gameProcessor.guessOutcomes().isEmpty());
    }
    @Test
    @DisplayName("Should win th game with correct guess")
    void testWinningGame() throws GameException{
        GuessOutcome[] guessOutcomes = gameProcessor.makeGuess("WATER");

        assertTrue(gameProcessor.isGameWon());
        assertTrue(gameProcessor.isGameOver());
        assertEquals(1, gameProcessor.getCurrentAttempt());

        for(GuessOutcome guessOutcome: guessOutcomes){
            assertEquals(GuessOutcome.CORRECT, guessOutcome);
        }
    }

    @Test
    @DisplayName("Should throw exception when making guess after game over")
    void testGuessAfterGameOver() throws GameException{
        gameProcessor.makeGuess("WATER");

        GameException ex = assertThrows(GameException.class, () -> {
            gameProcessor.makeGuess("CABLE");
        });

        assertEquals("Game is already over", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource( strings = {"" , "WA" , "W124" , "water!" , "Hellooooo"})
    @DisplayName("Should reject all invalid word formats")
    void testInvalidWordFormats(String invalidGuess){
        assertThrows(InvalidWordException.class, () -> {gameProcessor.makeGuess(invalidGuess);});
    }

    @Test
    @DisplayName("Should test case insensitive guess")
    void testCaseInsensitiveGuesses() throws GameException{
        GuessOutcome [] guesses = gameProcessor.makeGuess("water");

        assertTrue(gameProcessor.isGameWon());
        assertEquals("WATER" , gameProcessor.getGuess(0));
    }

    @Test
    @DisplayName("Should track multiple guesses correctly")
    void testTrackMultiplesGuesses() throws GameException{
        gameProcessor.makeGuess("CABLE");
        gameProcessor.makeGuess("HAPPY");

        assertEquals(2, gameProcessor.getCurrentAttempt());
        assertEquals( 2, gameProcessor.getGuesses().size());
        assertEquals("CABLE" , gameProcessor.getGuess(0));
        assertEquals("HAPPY" , gameProcessor.getGuess(1));
    }

    @Test
    @DisplayName("Should throw exception for invalid attempt number")
    void testInvalidAttemptNumber() throws GameException{
        gameProcessor.makeGuess("HAPPY");

        assertThrows(IndexOutOfBoundsException.class, () -> {gameProcessor.getGuess(1);});
        assertThrows(IndexOutOfBoundsException.class, () -> {gameProcessor.getGuess(-1);});
    }

    @Test
    @DisplayName("Should provide game summary")
    void testCorrectGameSummary() throws GameException{
        gameProcessor.makeGuess("HAPPY");
        String summary = gameProcessor.getGameSummary();

        assertTrue(summary.contains("IN PROGRESS"));
        assertTrue(summary.contains("Attempts: 1/5"));
        assertTrue(summary.contains("Remaining: 4"));

        gameProcessor.makeGuess("WATER");
        summary = gameProcessor.getGameSummary();

        assertTrue(summary.contains("WON"));
        assertTrue(summary.contains("Target Word: WATER"));
    }

    @Test
    @DisplayName("Should handle words not in wordList")
    void testWordNotInWordList() throws GameException{
        assertFalse(gameProcessor.isGuessInWordList("SSSSS"));
        assertDoesNotThrow(() -> {gameProcessor.makeGuess("SSSSS");});
    }

}