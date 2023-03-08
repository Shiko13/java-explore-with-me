package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError extends RuntimeException {

    public List<Error> errors;
    public String message;
    public String reason;
    public HttpStatus status;
    public LocalDateTime timestamp;
}
