package ru.practicum.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class DateException extends ApiError {
    public DateException(String message, LocalDateTime timestamp) {
        this.status = HttpStatus.BAD_REQUEST;
        this.reason = "Incorrectly time";
        this.message = message;
        this.timestamp = timestamp.withNano(0);
    }
}
