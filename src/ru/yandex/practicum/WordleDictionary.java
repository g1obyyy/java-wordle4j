package ru.yandex.practicum;

import ru.yandex.practicum.exception.WordleGameException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/*
    этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final List<String> words = new ArrayList<>();
    private final Random random = new Random();

    public final List<String> getWords() {
        return new ArrayList<>(words);
    }

    public final String getRandomWord() {
        if (isEmpty()) {
            throw new WordleGameException("Словарь пуст, невозможно выбрать слово");
        }
        return words.get(random.nextInt(size()));
    }

    public void addWord(final String word) {
        words.add(word);
    }

    public final String normalizeString(final String line) {
        Objects.requireNonNull(line);
        return line.trim().toLowerCase().replace('ё', 'е');
    }

    public boolean isRussian(final String line) {
        Objects.requireNonNull(line);
        for (int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if ((c < 'а' || c > 'я') && c != 'ё') {
                return false;
            }
        }
        return true;
    }

    public boolean isValid(final String word) {
        Objects.requireNonNull(word);
        return word.length() == 5 && isRussian(word);
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public int size() {
        return words.size();
    }

    public boolean contains(final String word) {
        return words.contains(word);
    }
}
