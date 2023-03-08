package ru.practicum.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class BadRequestException extends ApiError {
    public BadRequestException(String entityType, Long id, LocalDateTime timestamp) {
        this.status = HttpStatus.NOT_FOUND;
        this.reason = "The required object was not found.";
        this.message = entityType + " with id=" + id + " was not found ";
        this.timestamp = timestamp.withNano(0);
    }
}
