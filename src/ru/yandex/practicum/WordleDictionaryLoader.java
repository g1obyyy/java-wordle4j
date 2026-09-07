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
            throw new WordleTechnicalException("File not found: " + path.toAbsolutePath());
        }
        if (!Files.isRegularFile(path)) {
            throw new WordleTechnicalException("The file is not regular for that Path: " + path.toAbsolutePath());
        }
        if (!Files.isReadable(path)) {
            throw new WordleTechnicalException("That file is not readable for u: " + path.toAbsolutePath());
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
            logger.println("Dictionary is loaded. Words amount: " + dictionary.size());
        } catch (IOException e) {
            throw new WordleTechnicalException("Cannot read the dictionary: " + e.getMessage());
        }

        if (dictionary.isEmpty()) {
            throw new WordleTechnicalException("Dictionary is Empty!");
        }
        return dictionary;
    }
}
