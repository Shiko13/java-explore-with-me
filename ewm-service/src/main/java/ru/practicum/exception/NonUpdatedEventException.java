package ru.practicum.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class NonUpdatedEventException extends ApiError {
    public NonUpdatedEventException(String message, LocalDateTime timestamp) {
        this.status = HttpStatus.FORBIDDEN;
        this.reason = "For the requested operation the conditions are not met.";
        this.message = message;
        this.timestamp = timestamp.withNano(0);
    }
}
