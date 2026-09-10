package ru.yandex.practicum.exception;

public class WordNotFoundInDictionary extends WordleGameException {
    public WordNotFoundInDictionary(String message) {
        super(message);
    }
}
