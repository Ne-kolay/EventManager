package com.example.Event.manager.exceptions;

import jakarta.persistence.*;
import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.stream.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    //Validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return new ErrorMessageResponse(
                message,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }

    //Data retrieving exceptions
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageResponse handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        return new ErrorMessageResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }

    //Other exceptions
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorMessageResponse handleGeneral(
//            Exception ex,
//            HttpServletRequest request
//    ) {
//        return new ErrorMessageResponse(
//                "Internal server error",
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                request.getRequestURI(),
//                LocalDateTime.now()
//        );
//    }
}

