package ru.practicum.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ConflictException extends ApiError {
    public ConflictException(String message, LocalDateTime timestamp) {
        this.status = HttpStatus.CONFLICT;
        this.reason = "Integrity constraint has been violated.";
        this.message = message;
        this.timestamp = timestamp.withNano(0);
    }
}
