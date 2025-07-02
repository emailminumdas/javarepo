package com.wordle.game.util;

import com.wordle.game.logic.GuessOutcome;

public class ColorDisplayer {
    // ANSI escape codes for colors
    private static final String RESET = "\u001B[0m";
    private static final String GREEN_BG = "\u001B[42m";   // Green background for correct letters
    private static final String YELLOW_BG = "\u001B[43m";  // Yellow background for wrong position
    private static final String GRAY_BG = "\u001B[47m";    // Gray background for incorrect letters
    private static final String BLACK_TEXT = "\u001B[30m"; // Black text
    private static final String BOLD = "\u001B[1m";

    // Color codes for different message types
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";

    private ColorDisplayer() {
        throw new UnsupportedOperationException("ColorDisplayer class cannot be instantiated");
    }

    // Formats a single letter with appropriate background color based on guess guessOutcome.
    public static String formatLetter(char letter, GuessOutcome outcome) {
        if (letter == '\0' || outcome == null) {
            return "";
        }
        String background = switch (outcome) {
            case CORRECT -> GREEN_BG;
            case WRONG_POSITION -> YELLOW_BG;
            case INCORRECT -> GRAY_BG;
        };
        return String.format("%s%s %c %s", background, BLACK_TEXT,
                Character.toUpperCase(letter), RESET);
    }

    //Formats an entire guess with appropriate colors for each letter.
    public static String formatGuess(String guess, GuessOutcome[] outcomes) {
        if (guess == null || outcomes == null) {
            throw new IllegalArgumentException("Guess and outcome cannot be null");
        }

        if (guess.length() != outcomes.length) {
            throw new IllegalArgumentException("Guess length must match outcomes array length");
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < guess.length(); i++) {
            if (i > 0) {
                formatted.append(" ");
            }
            formatted.append(formatLetter(guess.charAt(i), outcomes[i]));
        }

        return formatted.toString();
    }

    //Formats an error message in red color.
    public static String error(String message) {
        return RED + BOLD + "ERROR: " + message + RESET;
    }

    //Formats a success message in green color.
    public static String success(String message) {
        return GREEN + BOLD + message + RESET;
    }

    //Formats a warning message in yellow color.
    public static String warning(String message) {
        return YELLOW + BOLD + "WARNING: " + message + RESET;
    }

    //Formats an info message in blue color.
    public static String info(String message) {
        return BLUE + message + RESET;
    }

    //Formats a title or header message in cyan color.

    public static String title(String message) {
        return CYAN + BOLD + message + RESET;
    }

    //Removes all ANSI escape codes from a string.
    public static String stripAnsiCodes(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("\u001B\\[[0-9;]*[a-zA-Z]", "");
    }

    //Checks if the current terminal likely supports ANSI colors.
    public static boolean isColorSupported() {
        String term = System.getenv("TERM");
        String os = System.getProperty("os.name").toLowerCase();

        return (term != null && !term.equals("dumb")) ||
                !os.contains("win") ||
                System.getenv("ANSICON") != null;
    }
}




