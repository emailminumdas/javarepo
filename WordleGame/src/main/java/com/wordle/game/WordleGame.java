package com.wordle.game;

import com.wordle.game.exceptions.GameException;
import com.wordle.game.exceptions.InvalidWordException;
import com.wordle.game.exceptions.WordLoadException;
import com.wordle.game.logic.GameProcessor;
import com.wordle.game.logic.GuessOutcome;
import com.wordle.game.util.ColorDisplayer;

import java.util.Scanner;

public class WordleGame {
    private static final Scanner scanner = new Scanner(System.in);
    private GameProcessor gameProcessor;

    public static void main(String[] args) {
        WordleGame game = new WordleGame();
        game.run();
    }

    public void run() {
        displayWelcome();

        try {
            do {
                playGame();
            } while (askPlayAgain());

        } catch (WordLoadException e) {
            System.err.println(ColorDisplayer.error("Failed to initialize game: " + e.getMessage()));
            System.exit(1);
        } finally {
            scanner.close();
        }

        displayGoodbye();
    }

    private void displayWelcome() {
        System.out.println();
        System.out.println(ColorDisplayer.title("🎯 WELCOME TO WORDLE! 🎯"));
        System.out.println();
        System.out.println(ColorDisplayer.info("GAME RULES:"));
        System.out.println("• You have 5 attempts to guess a 5-letter word");
        System.out.println("• Enter your guess and press Enter");
        System.out.println("• After each guess, letters will be colored:");
        System.out.println("  " + ColorDisplayer.formatLetter('G', GuessOutcome.CORRECT) + " = Correct letter in correct position");
        System.out.println("  " + ColorDisplayer.formatLetter('Y', GuessOutcome.WRONG_POSITION) + " = Correct letter in wrong position");
        System.out.println("  " + ColorDisplayer.formatLetter('R', GuessOutcome.INCORRECT) + " = Letter not in the word");
        System.out.println();
        System.out.println(ColorDisplayer.warning("NOTE: If you guess more of a letter than exists in the word,"));
        System.out.println(ColorDisplayer.warning("the extra letters will be marked as incorrect (gray)."));
        System.out.println();
    }

    //Plays a single game of Wordle.

    private void playGame() throws WordLoadException {
        try {
            gameProcessor = new GameProcessor();
            System.out.println(ColorDisplayer.success("🎮 NEW GAME STARTED! 🎮"));
            System.out.println(ColorDisplayer.info(String.format("Target word loaded from %d available words.",
                    gameProcessor.getWordCount())));
            System.out.println();

            while (!gameProcessor.isGameOver()) {
                displayGameState();
                processPlayerGuess();
            }

            displayGameguessOutcome();

        } catch (GameException e) {
            System.err.println(ColorDisplayer.error("Game error: " + e.getMessage()));
        }
    }

    //Displays the current game state including previous guesses.

    private void displayGameState() {
        System.out.println(ColorDisplayer.info(String.format("Attempt %d of %d:",
                gameProcessor.getCurrentAttempt() + 1,
                gameProcessor.getMaxAttempts())));

        // Display previous guesses if any
        var guesses = gameProcessor.getGuesses();
        var guessOutcomes = gameProcessor.guessOutcomes();

        for (int i = 0; i < guesses.size(); i++) {
            String guess = guesses.get(i);
            GuessOutcome[] guessOutcome = guessOutcomes.get(i);
            System.out.println(String.format("%d. %s", i + 1, ColorDisplayer.formatGuess(guess, guessOutcome)));
        }

        if (!guesses.isEmpty()) {
            System.out.println();
        }
    }

    //Processes a single player guess with input validation and error handling.

    private void processPlayerGuess() {
        while (true) {
            System.out.print("Enter your guess: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println(ColorDisplayer.warning("Please enter a 5-letter word."));
                continue;
            }
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println(ColorDisplayer.info("Game terminated by user."));
                System.exit(0);
            }

            try {
                // Check if guess is in word list (optional warning)
                if (!gameProcessor.isGuessInWordList(input)) {
                    System.out.println(ColorDisplayer.warning("Word not in dictionary, but allowed as per game rules."));
                }

                GuessOutcome[] guessOutcomes = gameProcessor.makeGuess(input);

                // Display the guessOutcome immediately
                System.out.println(ColorDisplayer.formatGuess(input.toUpperCase(), guessOutcomes));
                System.out.println();

                break; // Valid guess processed, exit loop

            } catch (InvalidWordException e) {
                System.out.println(ColorDisplayer.error(e.getMessage()));
                System.out.println(ColorDisplayer.info("Please enter exactly 5 letters (A-Z only)."));
            } catch (GameException e) {
                System.out.println(ColorDisplayer.error(e.getMessage()));
                break; // Game state error, exit guess loop
            }
        }
    }

    //Displays the final game guessOutcome (win or lose).
    private void displayGameguessOutcome() {
        System.out.println("=".repeat(50));

        if (gameProcessor.isGameWon()) {
            System.out.println(ColorDisplayer.success("🎉 CONGRATULATIONS! YOU WON! 🎉"));
            System.out.println(ColorDisplayer.success(String.format("You guessed the word '%s' in %d attempts!",
                    gameProcessor.getTargetWord(),
                    gameProcessor.getCurrentAttempt())));
        } else {
            System.out.println(ColorDisplayer.error("💀 GAME OVER! 💀"));
            System.out.println(ColorDisplayer.info(String.format("The word was: %s",
                    gameProcessor.getTargetWord())));
            System.out.println(ColorDisplayer.info("Better luck next time!"));
        }

        System.out.println();
        displayGameStatistics();
        System.out.println("=".repeat(50));
    }

    //Displays game statistics and summary.
    private void displayGameStatistics() {
        System.out.println(ColorDisplayer.title("📊 GAME SUMMARY 📊"));
        System.out.println(String.format("Attempts used: %d/%d",
                gameProcessor.getCurrentAttempt(),
                gameProcessor.getMaxAttempts()));

        var guesses = gameProcessor.getGuesses();
        var guessOutcomes = gameProcessor.guessOutcomes();

        System.out.println("\nYour guesses:");
        for (int i = 0; i < guesses.size(); i++) {
            String guess = guesses.get(i);
            GuessOutcome[] guessOutcome = guessOutcomes.get(i);
            System.out.println(String.format("%d. %s", i + 1, ColorDisplayer.formatGuess(guess, guessOutcome)));
        }
        System.out.println();
    }

    //Check with the player if they want to play another game.
    private boolean askPlayAgain() {
        while (true) {
            System.out.print("Would you like to play again? (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "y", "yes", "1", "true" -> {
                    return true;
                }
                case "n", "no", "0", "false" -> {
                    return false;
                }
                default -> {
                    System.out.println(ColorDisplayer.warning("Please enter 'y' for yes or 'n' for no."));
                }
            }
        }
    }

    //Displays the goodbye message.

    private void displayGoodbye() {
        System.out.println();
        System.out.println(ColorDisplayer.title("Thanks for playing Wordle! 👋"));
        System.out.println(ColorDisplayer.info("Come back soon for more word puzzles!"));
        System.out.println();
    }

    private void handleUnexpectedError(Exception e) {
        System.err.println(ColorDisplayer.error("An unexpected error occurred: " + e.getMessage()));
        if (e.getCause() != null) {
            System.err.println(ColorDisplayer.error("Caused by: " + e.getCause().getMessage()));
        }
        System.err.println(ColorDisplayer.info("Please try restarting the game."));
        e.printStackTrace(System.err);
    }
}