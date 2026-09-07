package ru.yandex.practicum;

import ru.yandex.practicum.exception.WordleTechnicalException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class WordleDictionaryLoader {
    private final Path path;
    private final PrintWriter logger;

    private WordleDictionaryLoader(final Path path, final PrintWriter logger) {
        this.path = path;
        this.logger = logger;
    }

    public static WordleDictionaryLoader of(String filename, PrintWriter logger) throws WordleTechnicalException {
        Objects.requireNonNull(filename);

        Path path = Path.of(filename);
        if (!Files.exists(path)) {
            throw new WordleTechnicalException("Файл не найден: " + path.toAbsolutePath());
        }
        if (!Files.isRegularFile(path)) {
            throw new WordleTechnicalException("По такому пути лежит не обычный файл: " + path.toAbsolutePath());
        }
        if (!Files.isReadable(path)) {
            throw new WordleTechnicalException("У вас нет прав на чтение этого файла: " + path.toAbsolutePath());
        }

        return new WordleDictionaryLoader(path, logger);
    }

    public WordleDictionary load() throws WordleTechnicalException {
        WordleDictionary dictionary = new WordleDictionary();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = dictionary.normalizeString(line);
                if (dictionary.isValid(word)) {
                    dictionary.addWord(word);
                }
            }
            logger.println("Словарь загружен. Количество загруженных слов: " + dictionary.size());
        } catch (IOException e) {
            throw new WordleTechnicalException("Ошибка чтения словаря: " + e.getMessage());
        }

        if (dictionary.isEmpty()) {
            throw new WordleTechnicalException("Словарь пуст!");
        }
        return dictionary;
    }
}
