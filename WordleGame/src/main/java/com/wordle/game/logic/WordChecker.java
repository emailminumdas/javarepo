package com.wordle.game.logic;

import com.wordle.game.exceptions.InvalidWordException;

import java.util.HashMap;
import java.util.Map;

public class WordChecker {
    private static final int WORD_LENGTH = 5;

    public GuessOutcome[] validateGuess(String guessWord, String targetWord) throws InvalidWordException {
        validateInputs(guessWord, targetWord);
        String updatedGuessWord = guessWord.toUpperCase();
        String updatedTargetWord = targetWord.toUpperCase();
        GuessOutcome[] guessOutcomes = new GuessOutcome[WORD_LENGTH];
        // First, count letter frequencies in the target word
        Map<Character, Integer> targetLetterCount = countLetters(updatedTargetWord);

        // First pass: Mark correct positions (green)
        for (int i = 0; i < WORD_LENGTH; i++) {
            char guessChar = updatedGuessWord.charAt(i);
            char targetChar = updatedTargetWord.charAt(i);

            if (guessChar == targetChar) {
                guessOutcomes[i] = GuessOutcome.CORRECT;
                // Decrease the count for this letter since we've used it
                targetLetterCount.put(guessChar, targetLetterCount.get(guessChar) - 1);
            }
        }
        // Second pass: Mark wrong positions (yellow) and incorrect (gray)
        for (int i = 0; i < WORD_LENGTH; i++) {
            // Skip letters already marked as correct
            if (guessOutcomes[i] == GuessOutcome.CORRECT) {
                continue;
            }

            char guessChar = updatedGuessWord.charAt(i);
            int remainingCount = targetLetterCount.getOrDefault(guessChar, 0);

            if (remainingCount > 0) {
                guessOutcomes[i] = GuessOutcome.WRONG_POSITION;
                // Decrease the count for this letter
                targetLetterCount.put(guessChar, remainingCount - 1);
            } else {
                guessOutcomes[i] = GuessOutcome.INCORRECT;
            }
        }

        return guessOutcomes;
    }

    private Map<Character, Integer> countLetters(String word) {
        Map<Character, Integer> letterCount = new HashMap<>();

        for (char c : word.toCharArray()) {
            letterCount.put(c, letterCount.getOrDefault(c, 0) + 1);
        }

        return letterCount;
    }

    private void validateInputs(String guessWord, String targetWord) {
        if (guessWord == null) throw new InvalidWordException("null", "Guess word cannot be null");
        if (targetWord == null) throw new InvalidWordException("null", "Target word cannot be null");
        if (guessWord.length() != WORD_LENGTH) {
            throw new InvalidWordException(guessWord,
                    String.format("Must be exactly %d letters long (was %d)", WORD_LENGTH, guessWord.length()));
        }

        if (targetWord.length() != WORD_LENGTH) {
            throw new InvalidWordException(targetWord,
                    String.format("Target must be exactly %d letters long (was %d)", WORD_LENGTH, targetWord.length()));
        }

        if (!guessWord.chars().allMatch(Character::isLetter)) {
            throw new InvalidWordException(guessWord, "Must contain only alphabetic characters");
        }

        if (!targetWord.chars().allMatch(Character::isLetter)) {
            throw new InvalidWordException(targetWord, "Target must contain only alphabetic characters");
        }
    }

    public boolean checkForExactMatch(String guess, String target) throws InvalidWordException {
        validateInputs(guess, target);
        return guess.equalsIgnoreCase(target);
    }

    public int getCorrectCount(GuessOutcome[] guessOutcomes) {
        if (guessOutcomes == null) {
            return 0;
        }

        int count = 0;
        for (GuessOutcome guessOutcome : guessOutcomes) {
            if (guessOutcome == GuessOutcome.CORRECT) {
                count++;
            }
        }
        return count;
    }

    public int getWrongPositionCount(GuessOutcome[] guessOutcomes) {
        if (guessOutcomes == null) {
            return 0;
        }

        int count = 0;
        for (GuessOutcome guessOutcome : guessOutcomes) {
            if (guessOutcome == GuessOutcome.WRONG_POSITION) {
                count++;
            }
        }
        return count;
    }

    public String getguessOutcomeSummary(GuessOutcome[] guessOutcomes) {
        if (guessOutcomes == null || guessOutcomes.length == 0) {
            return "No guessOutcomes";
        }

        int correct = getCorrectCount(guessOutcomes);
        int wrongPosition = getWrongPositionCount(guessOutcomes);
        int incorrect = guessOutcomes.length - correct - wrongPosition;

        return String.format("Correct: %d, Wrong Position: %d, Incorrect: %d", correct, wrongPosition, incorrect);
    }
}


