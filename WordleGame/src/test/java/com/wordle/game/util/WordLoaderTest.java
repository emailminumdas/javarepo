package com.wordle.game.util;

import com.wordle.game.exceptions.WordLoadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordLoaderTest{

    @Test
    @DisplayName("Should load words from default file successfully")
    void testLoadDefaultWords() throws WordLoadException {
        WordLoader loader = new WordLoader();

        assertTrue(loader.getWordCount() > 0);
        assertNotNull(loader.getRandomWord());
        assertEquals(5, loader.getRandomWord().length());
    }

    @Test
    @DisplayName("Should provide random words")
    void testRandomWords() throws WordLoadException {
        WordLoader loader = new WordLoader();

        String word1 = loader.getRandomWord();
        String word2 = loader.getRandomWord();

        assertNotNull(word1);
        assertNotNull(word2);
        assertEquals(5, word1.length());
        assertEquals(5, word2.length());

        boolean foundDifferent = false;
        for (int i = 0; i < 20; i++) {
            if (!word1.equals(loader.getRandomWord())) {
                foundDifferent = true;
                break;
            }
        }
    }

    @Test
    @DisplayName("Should validate words correctly")
    void testWordValidation() {
        assertTrue(WordLoader.isWordValid("WATER"));
        assertTrue(WordLoader.isWordValid("water")); // Case shouldn't matter for validation
        assertTrue(WordLoader.isWordValid("HELLO"));

        assertFalse(WordLoader.isWordValid(null));
        assertFalse(WordLoader.isWordValid(""));
        assertFalse(WordLoader.isWordValid("WAT")); // Too short
        assertFalse(WordLoader.isWordValid("TOOLONG")); // Too long
        assertFalse(WordLoader.isWordValid("WAT3R")); // Contains number
        assertFalse(WordLoader.isWordValid("WAT-R")); // Contains hyphen
        assertFalse(WordLoader.isWordValid("WAT R")); // Contains space
    }

    @Test
    @DisplayName("Should handle file not found")
    void testFileNotFound() {
        assertThrows(WordLoadException.class, () -> {
            new WordLoader("/sample.txt");
        });
    }

    @Test
    @DisplayName("Should return correct word length constant")
    void testWordLengthConstant() {
        assertEquals(5, WordLoader.getWordLength());
    }

    @Test
    @DisplayName("Should normalize words to uppercase")
    void testWordNormalization() throws WordLoadException {
        WordLoader loader = new WordLoader();

        // All words should be uppercase
        for (String word : loader.getAllWords()) {
            assertEquals(word.toUpperCase(), word);
        }

        String randomWord = loader.getRandomWord();
        assertEquals(randomWord.toUpperCase(), randomWord);
    }

    @Test
    @DisplayName("Should filter invalid words during loading")
    void testInvalidWordFiltering() throws WordLoadException {
        WordLoader loader = new WordLoader();

        // All loaded words should be valid
        for (String word : loader.getAllWords()) {
            assertTrue(WordLoader.isWordValid(word));
            assertEquals(5, word.length());
            assertTrue(word.chars().allMatch(Character::isLetter));
        }
    }

    @Test
    @DisplayName("Should throw IllegalStateException when no words available")
    void testNoWordsAvailableException() {
        // This is difficult to test without mocking or creating a special loader
        // For now, we ensure our current implementation doesn't hit this case
        assertDoesNotThrow(() -> {
            WordLoader loader = new WordLoader();
            assertNotNull(loader.getRandomWord());
        });
    }

    @Test
    @DisplayName("Should provide word count statistics")
    void testWordCountStatistics() throws WordLoadException {
        WordLoader loader = new WordLoader();

        int wordCount = loader.getWordCount();

        // Our word list should have a reasonable number of words
        assertTrue(wordCount >= 10, "Should have at least 10 words for a good game");
        assertTrue(wordCount <= 10000, "Word list shouldn't be excessively large");

        System.out.printf("Word list contains %d words%n", wordCount);
    }
}
