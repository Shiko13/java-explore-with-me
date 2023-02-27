package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.exception.*;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(InvalidIdException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final InvalidIdException e) {
        log.error("HTTP status code 404 - " + e.getMessage());
        return new ErrorResponse(e);
    }

    @ExceptionHandler({DataException.class,
            NotPendingStatusException.class,
                ForbiddenException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final ApiError e) {
        log.error("HTTP status code 409 - " + e.getMessage());
        return new ErrorResponse(e);
    }

    @ExceptionHandler({ValidateConflictException.class,
            NonUpdatedEventException.class,
            ParticipantLimitException.class,
            CategoryIsNotEmptyException.class,
            })
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final ApiError e) {
        log.error("HTTP status code 409 - " + e.getMessage());
        return new ErrorResponse(e);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownBookingState(final MethodArgumentTypeMismatchException e) {
        log.error("HTTP status code 400 - {}", e.getMessage());
        ApiError apiError = new ApiError(new ArrayList<>(), e.getMessage(), "TIncorrectly made request.",
                HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return new ErrorResponse(apiError);
    }

    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleValidateParameterException(final PersistenceException e) {
        log.error("HTTP status code 409: {}", e.getMessage());
        ApiError apiError = new ApiError(new ArrayList<>(), e.getMessage(), "Integrity constraint has been violated.",
                HttpStatus.CONFLICT, LocalDateTime.now());
        return new ErrorResponse(apiError);
    }

}
