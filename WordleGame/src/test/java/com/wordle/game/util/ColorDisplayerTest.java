
package com.wordle.game.util;

import com.wordle.game.logic.GuessOutcome;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class ColorDisplayerTest {

    @Test
    @DisplayName("Should format single letters with correct colors")
    void testFormatLetter() {
        String greenLetter = ColorDisplayer.formatLetter('A', GuessOutcome.CORRECT);
        String yellowLetter = ColorDisplayer.formatLetter('B', GuessOutcome.WRONG_POSITION);
        String grayLetter = ColorDisplayer.formatLetter('C', GuessOutcome.INCORRECT);

        // Should contain the letter and ANSI codes
        assertTrue(greenLetter.contains("A"));
        assertTrue(yellowLetter.contains("B"));
        assertTrue(grayLetter.contains("C"));

        // Should contain reset codes
        assertTrue(greenLetter.contains("\u001B[0m"));
        assertTrue(yellowLetter.contains("\u001B[0m"));
        assertTrue(grayLetter.contains("\u001B[0m"));

        // Letters should be uppercase
        String lowerCaseLetter = ColorDisplayer.formatLetter('a', GuessOutcome.CORRECT);
        assertTrue(lowerCaseLetter.contains("A"));
    }

    @ParameterizedTest
    @EnumSource(GuessOutcome.class)
    @DisplayName("Should handle all GuessOutcome types")
    void testAllGuessOutcomeTypes(GuessOutcome guessOutcome) {
        String formatted = ColorDisplayer.formatLetter('X', guessOutcome);

        assertNotNull(formatted);
        assertTrue(formatted.contains("X"));
        assertTrue(formatted.contains("\u001B[0m")); // Reset code
    }

    @Test
    @DisplayName("Should format complete guess correctly")
    void testFormatGuess() {
        String guess = "WATER";
        GuessOutcome[] guessOutcomes = {
                GuessOutcome.CORRECT,
                GuessOutcome.WRONG_POSITION,
                GuessOutcome.INCORRECT,
                GuessOutcome.CORRECT,
                GuessOutcome.WRONG_POSITION
        };

        String formatted = ColorDisplayer.formatGuess(guess, guessOutcomes);

        // Should contain all letters
        assertTrue(formatted.contains("W"));
        assertTrue(formatted.contains("A"));
        assertTrue(formatted.contains("T"));
        assertTrue(formatted.contains("E"));
        assertTrue(formatted.contains("R"));

        // Should contain reset codes
        assertTrue(formatted.contains("\u001B[0m"));
    }

    @Test
    @DisplayName("Should handle null inputs gracefully")
    void testNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> {
            ColorDisplayer.formatGuess(null, new GuessOutcome[5]);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ColorDisplayer.formatGuess("WATER", null);
        });
    }

    @Test
    @DisplayName("Should format different message types correctly")
    void testMessageFormatting() {
        String error = ColorDisplayer.error("Test error");
        String success = ColorDisplayer.success("Test success");
        String warning = ColorDisplayer.warning("Test warning");
        String info = ColorDisplayer.info("Test info");
        String title = ColorDisplayer.title("Test title");

        // All should contain the message text
        assertTrue(error.contains("Test error"));
        assertTrue(success.contains("Test success"));
        assertTrue(warning.contains("Test warning"));
        assertTrue(info.contains("Test info"));
        assertTrue(title.contains("Test title"));

        // All should contain reset codes
        assertTrue(error.contains("\u001B[0m"));
        assertTrue(success.contains("\u001B[0m"));
        assertTrue(warning.contains("\u001B[0m"));
        assertTrue(info.contains("\u001B[0m"));
        assertTrue(title.contains("\u001B[0m"));

        // Error should contain "ERROR:" prefix
        assertTrue(error.contains("ERROR:"));

        // Warning should contain "WARNING:" prefix
        assertTrue(warning.contains("WARNING:"));
    }

    @Test
    @DisplayName("Should format numbers as letters correctly")
    void testNumberFormatting() {
        String formatted = ColorDisplayer.formatLetter('5', GuessOutcome.CORRECT);
        assertTrue(formatted.contains("5"));
    }
}
