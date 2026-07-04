package com.aryan.resumeai.common.exception;

import com.aryan.resumeai.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ResourceNotFoundException.class
    )
    public ResponseEntity<ErrorResponse>
    handleResourceNotFound(
            ResourceNotFoundException ex
    ) {

        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.builder()
                                .message(
                                        ex.getMessage()
                                )
                                .status(404)
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }

    @ExceptionHandler(
            RuntimeException.class
    )
    public ResponseEntity<ErrorResponse>
    handleRuntimeException(
            RuntimeException ex
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        ErrorResponse.builder()
                                .message(
                                        ex.getMessage()
                                )
                                .status(400)
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse>
    handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        String message =
                ex.getBindingResult()
                        .getFieldError()
                        .getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(
                        ErrorResponse.builder()
                                .message(message)
                                .status(400)
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }

    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ErrorResponse>
    handleException(
            Exception ex
    ) {

        ex.printStackTrace();
        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        ErrorResponse.builder()
                                .message(
                                        ex.getMessage()
                                )
                                .status(500)
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }
}