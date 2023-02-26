package ru.practicum.exception;

public class NotPendingStatusException extends RuntimeException {
    public NotPendingStatusException(String message) {
        super(message);
    }
}
