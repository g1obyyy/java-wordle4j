package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exception.WordleTechnicalException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    private PrintWriter logger;
    private StringWriter logOutput;

    @BeforeEach
    void setUp() {
        logOutput = new StringWriter();
        logger = new PrintWriter(logOutput);
    }

    @Test
    void shouldLoadDictionarySuccessfully(@TempDir Path tempDir) throws IOException, WordleTechnicalException {
        // Подготовка: создаем файл со словарем, содержащим валидные слова, с пробелами и буквой 'ё'
        Path dictionaryFile = tempDir.resolve("valid_words.txt");
        Files.writeString(dictionaryFile, " кошка \n СОБАК \n МЁДЫЫ \n 12345 \n кот \n");

        // Действие
        WordleDictionaryLoader loader = WordleDictionaryLoader.of(dictionaryFile.toString(), logger);
        WordleDictionary dictionary = loader.load();

        // Проверки
        assertNotNull(dictionary);
        assertEquals(3, dictionary.size());
        assertTrue(dictionary.contains("кошка"));
        assertTrue(dictionary.contains("собак"));
        assertTrue(dictionary.contains("медыы")); // Проверка замены ё -> е
        assertFalse(dictionary.contains("мёдыы"));
    }

    @Test
    void shouldThrowExceptionWhenFileDoesNotExist(@TempDir Path tempDir) {
        Path nonExistentFile = tempDir.resolve("missing.txt");

        WordleTechnicalException exception = assertThrows(WordleTechnicalException.class, () -> {
            WordleDictionaryLoader.of(nonExistentFile.toString(), logger);
        });

        assertTrue(exception.getMessage().contains("Файл не найден"));
    }

    @Test
    void shouldThrowExceptionWhenPathIsDirectory(@TempDir Path tempDir) {
        // Передаем путь к директории вместо файла
        WordleTechnicalException exception = assertThrows(WordleTechnicalException.class, () -> {
            WordleDictionaryLoader.of(tempDir.toString(), logger);
        });

        assertTrue(exception.getMessage().contains("лежит не обычный файл"));
    }

    @Test
    void shouldThrowExceptionWhenDictionaryIsEmpty(@TempDir Path tempDir) throws IOException, WordleTechnicalException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.createFile(emptyFile);

        WordleDictionaryLoader loader = WordleDictionaryLoader.of(emptyFile.toString(), logger);

        WordleTechnicalException exception = assertThrows(WordleTechnicalException.class, loader::load);

        assertTrue(exception.getMessage().contains("Словарь пуст!"));
    }

    @Test
    void shouldThrowExceptionWhenNoValidWordsFound(@TempDir Path tempDir) throws IOException, WordleTechnicalException {
        Path invalidWordsFile = tempDir.resolve("invalid.txt");
        // Ни одно слово не подходит под размер 5 и русскую раскладку
        Files.writeString(invalidWordsFile, "cat\n12345\nдлинноеслово\n");

        WordleDictionaryLoader loader = WordleDictionaryLoader.of(invalidWordsFile.toString(), logger);

        WordleTechnicalException exception = assertThrows(WordleTechnicalException.class, loader::load);

        assertTrue(exception.getMessage().contains("Словарь пуст!"));
    }
}