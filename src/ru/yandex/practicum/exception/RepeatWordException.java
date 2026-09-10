package ru.yandex.practicum.exception;

public class RepeatWordException extends WordleGameException {
    public RepeatWordException(String message) {
        super(message);
    }
}
