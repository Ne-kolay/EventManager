package com.example.event.manager.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Validation errors (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String detailedMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorMessageResponse body = new ErrorMessageResponse(
                "Validation failed",
                detailedMessage,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Not found errors (404)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorMessageResponse body = new ErrorMessageResponse(
                "Entity not found",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    //Invalid credentials (401)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorMessageResponse> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request
    ) {
        ErrorMessageResponse body = new ErrorMessageResponse(
                "Invalid credentials",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }


    @ExceptionHandler(LoginAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageResponse> handleLoginAlreadyExists(
            LoginAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ErrorMessageResponse body = new ErrorMessageResponse(
                "Login already exists",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }


    // General errors (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception", ex);
        ErrorMessageResponse body = new ErrorMessageResponse(
                "Internal server error",
                "Unexpected error occurred",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
