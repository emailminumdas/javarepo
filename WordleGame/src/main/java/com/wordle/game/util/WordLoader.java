package com.wordle.game.util;

import com.wordle.game.exceptions.WordLoadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class WordLoader {
    private static final int WORD_LENGTH = 5;
    private static final String WORD_FILE = "/wordList.txt";
    private final List<String> words;
    private final Random random;

    public WordLoader() throws WordLoadException {
        this(WORD_FILE);
    }

    public WordLoader(String fileName) throws WordLoadException {
        this.random = new Random();
        this.words = loadWordsFromFile(fileName);
        if (words.isEmpty()) {
            throw new WordLoadException(fileName, "No valid words found in file");
        }
    }

    private List<String> loadWordsFromFile(String fileName) throws WordLoadException {
        List<String> loadedWords = new ArrayList<>();
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new WordLoadException(fileName, "File not found in resources");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                loadedWords = reader.lines()
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .filter(this::isValidWord)
                        .distinct()
                        .collect(Collectors.toList());
            }

        } catch (IOException e) {
            throw new WordLoadException(fileName, "I/O error while reading file", e);
        }

        return loadedWords;
    }

    private boolean isValidWord(String word) {
        if (word == null || word.length() != WORD_LENGTH) {
            return false;
        }
        // Check if word contains only alphabetic characters
        return word.chars().allMatch(Character::isLetter);

    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("No words available");
        }

        return words.get(random.nextInt(words.size()));
    }

    public boolean isWordInList(String word) {
        if (word == null) {
            return false;
        }

        return words.contains(word.toUpperCase());
    }

    public int getWordCount() {
        return words.size();
    }

    public List<String> getAllWords() {
        return Collections.unmodifiableList(words);
    }

    public static boolean isWordValid(String word) {
        return word != null &&
                word.length() == WORD_LENGTH &&
                word.chars().allMatch(Character::isLetter);
    }

    public static int getWordLength() {
        return WORD_LENGTH;
    }
}
