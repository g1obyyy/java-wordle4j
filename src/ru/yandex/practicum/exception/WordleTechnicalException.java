package ru.yandex.practicum.exception;

public class WordleTechnicalException extends Exception {
    public WordleTechnicalException(String message) {
        super(message);
    }

    public WordleTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
