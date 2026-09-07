package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.RepeatWordException;
import ru.yandex.practicum.exception.WordNotFoundInDictionary;
import ru.yandex.practicum.exception.WordleGameException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private WordleDictionary dictionary;
    private PrintWriter dummyLogger;

    @BeforeEach
    void setUp() {
        // Создаем чистый словарь и заглушку для логгера перед каждым тестом
        dictionary = new WordleDictionary();
        dummyLogger = new PrintWriter(new StringWriter());
    }

    @Test
    void normalizeString_shouldTrimLowercaseAndReplaceYo() {
        String result = dictionary.normalizeString("  ЁЖИКИ  ");
        assertEquals("ежики", result, "Строка должна быть в нижнем регистре, без пробелов, ё заменена на е");
    }

    @Test
    void isRussian_validCyrillic_returnsTrue() {
        assertTrue(dictionary.isRussian("абвгд"));
        assertTrue(dictionary.isRussian("ёжики"));
    }

    @Test
    void isRussian_withEnglishOrNumbers_returnsFalse() {
        assertFalse(dictionary.isRussian("abcde"));
        assertFalse(dictionary.isRussian("слов1"));
        assertFalse(dictionary.isRussian("сло-в"));
    }

    @Test
    void isValid_onlyFiveLetterRussianWords_returnsTrue() {
        assertTrue(dictionary.isValid("кошка"));
        assertFalse(dictionary.isValid("кот")); // Меньше 5
        assertFalse(dictionary.isValid("собака")); // Больше 5
        assertFalse(dictionary.isValid("apple")); // Не русское
    }


    @Test
    void makeMove_perfectMatch_returnsAllPlusAndWins() {
        dictionary.addWord("кошка");
        WordleGame game = new WordleGame("кошка", dictionary, dummyLogger);

        String feedback = game.makeMove("кошка");

        assertEquals("+++++", feedback);
        assertTrue(game.isWin());
        assertTrue(game.isOver());
    }

    @Test
    void makeMove_completelyWrongWord_returnsAllMinus() {
        dictionary.addWord("кошка");
        dictionary.addWord("билет");
        WordleGame game = new WordleGame("кошка", dictionary, dummyLogger);

        String feedback = game.makeMove("билет");

        assertEquals("-----", feedback);
        assertFalse(game.isWin());
        assertEquals(5, game.getAttempts());
    }

    @Test
    void makeMove_partialMatch_returnsCorrectMask() {
        dictionary.addWord("банан");
        dictionary.addWord("кабан");
        WordleGame game = new WordleGame("банан", dictionary, dummyLogger);

        String feedback = game.makeMove("кабан");
        assertEquals("-+^++", feedback);
    }

    @Test
    void makeMove_duplicateLettersLogic_marksCorrectly() {
        dictionary.addWord("топот");
        dictionary.addWord("пепел");
        dictionary.addWord("потоп");

        WordleGame game = new WordleGame("топот", dictionary, dummyLogger);
        String feedback = game.makeMove("потоп");

        assertEquals("^+^+-", feedback);
    }

    @Test
    void makeMove_wordNotInDictionary_throwsException() {
        dictionary.addWord("кошка");
        WordleGame game = new WordleGame("кошка", dictionary, dummyLogger);

        assertThrows(WordNotFoundInDictionary.class, () -> game.makeMove("собака"));
        assertThrows(WordNotFoundInDictionary.class, () -> game.makeMove("ааааа"));
        assertEquals(6, game.getAttempts());
    }

    @Test
    void makeMove_repeatWord_throwsException() {
        dictionary.addWord("кошка");
        dictionary.addWord("песик");
        WordleGame game = new WordleGame("кошка", dictionary, dummyLogger);

        game.makeMove("песик");

        assertThrows(RepeatWordException.class, () -> game.makeMove("песик"));
        assertEquals(5, game.getAttempts());
    }

    @Test
    void getHint_filtersCandidatesCorrectly() {
        dictionary.addWord("норка");
        dictionary.addWord("корка");
        dictionary.addWord("горка");
        dictionary.addWord("порча");

        WordleGame game = new WordleGame("горка", dictionary, dummyLogger);

        game.makeMove("норка");
        String hint = game.getHint();
        assertTrue(hint.equals("корка") || hint.equals("горка"));
        assertNotEquals("порча", hint);
        assertNotEquals("норка", hint);
    }
}