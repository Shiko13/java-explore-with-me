package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private final HttpStatus status;
    private final String message;
    private final String reason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public ErrorResponse(ApiError e) {
        this.status = e.getStatus();
        this.reason = e.getReason();
        this.message = e.getMessage();
        this.timestamp = e.getTimestamp();
    }
}
