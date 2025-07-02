
package com.wordle.game.logic;

import com.wordle.game.exceptions.InvalidWordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordCheckerTest{
    private WordChecker checker;

    @BeforeEach
    void setUp() {
        checker = new WordChecker();
    }

    @Test
    @DisplayName("Should check exact word correctly")
    void testExactMatch() throws InvalidWordException {
        assertTrue(checker.checkForExactMatch("WATER", "WATER"));
        assertTrue(checker.checkForExactMatch("WATER", "water"));
        assertFalse(checker.checkForExactMatch("ABOUT","WATER"));
    }

    @Test
    @DisplayName("Should handle perfect guess")
    void testPerfectGuess() throws InvalidWordException {
        GuessOutcome[] guessOutcomes = checker.validateGuess("WATER","WATER");
        assertEquals(5, guessOutcomes.length);
        for(GuessOutcome guessOutcome : guessOutcomes){
            assertEquals(GuessOutcome.CORRECT, guessOutcome);
        }
    }

    @Test
    @DisplayName("Should handle wrong guess")
    void testWrongGuess()  throws  InvalidWordException{
        GuessOutcome[] guessOutcomes = checker.validateGuess("LUCKY","WATER");
        assertEquals(5, guessOutcomes.length);
        for(GuessOutcome guessOutcome : guessOutcomes){
            assertEquals(GuessOutcome.INCORRECT, guessOutcome);
        }
    }

    @Test
    @DisplayName("Should validate input parameters")
    void testInputValidation() {
        // Null inputs
        assertThrows(InvalidWordException.class, () -> {
            checker.validateGuess(null, "WATER");
        });

        assertThrows(InvalidWordException.class, () -> {
            checker.validateGuess("WATER", null);
        });

        // Wrong length
        assertThrows(InvalidWordException.class, () -> {
            checker.validateGuess("WAT", "WATER");
        });

        assertThrows(InvalidWordException.class, () -> {
            checker.validateGuess("WATER", "WAT");
        });

        // Non-alphabetic characters
        assertThrows(InvalidWordException.class, () -> {
            checker.validateGuess("WAT3R", "WATER");
        });

        assertThrows(InvalidWordException.class, () -> {
            checker.validateGuess("WATER", "WAT-R");
        });
    }

    @Test
    @DisplayName("Should be case insensitive")
    void testCaseInsensitivity() throws InvalidWordException {
        GuessOutcome[] guessOutcome1 = checker.validateGuess("water", "WATER");
        GuessOutcome[] guessOutcome2 = checker.validateGuess("WATER", "water");
        GuessOutcome[] guessOutcome3 = checker.validateGuess("WaTeR", "wAtEr");

        // All should be perfect matches
        for (int i = 0; i < 5; i++) {
            assertEquals(GuessOutcome.CORRECT, guessOutcome1[i]);
            assertEquals(GuessOutcome.CORRECT, guessOutcome2[i]);
            assertEquals(GuessOutcome.CORRECT, guessOutcome3[i]);
        }
    }

    @Test
    @DisplayName("Should handle null guessOutcomes in utility methods")
    void testNullguessOutcomesHandling() {
        assertEquals(0, checker.getCorrectCount(null));
        assertEquals(0, checker.getWrongPositionCount(null));
        assertEquals("No guessOutcomes", checker.getguessOutcomeSummary(null));
        assertEquals("No guessOutcomes", checker.getguessOutcomeSummary(new GuessOutcome[0]));
    }
}
