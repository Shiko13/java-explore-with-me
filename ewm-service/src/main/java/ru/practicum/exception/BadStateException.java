package ru.practicum.exception;

public class BadStateException extends RuntimeException {
    public BadStateException(String message) {
        super(message);
    }
}
