package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {
    private final Path path;

    private WordleDictionaryLoader(final Path path) {
        this.path = path;
    }

    public static WordleDictionaryLoader of(String filename) throws IOException {
        Objects.requireNonNull(filename);

        Path path = Path.of(filename);
        if (!Files.exists(path)) {
            throw new IOException("Файл не найден:" + path.toAbsolutePath());
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("По заданному пути файл не является обычным: " + path.toAbsolutePath());
        }
        if (!Files.isReadable(path)) {
            throw new IOException("У вас нет прав для чтения файла: " + path.toAbsolutePath());
        }

        return new WordleDictionaryLoader(path);
    }

    public WordleDictionary load() throws IOException {
        WordleDictionary dictionary = new WordleDictionary();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = dictionary.normalizeString(line);
                if (dictionary.isValid(word)) {
                    dictionary.addWord(word);
                }
            }
        }

        if (dictionary.isEmpty()) {
            throw new IOException("Словарь пуст! В нем нет подходящих слов для участия в игре." + path.toAbsolutePath());
        }
        return dictionary;
    }
}
