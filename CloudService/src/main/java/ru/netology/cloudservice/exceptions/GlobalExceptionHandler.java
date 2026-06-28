package ru.netology.cloudservice.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudservice.dto.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(
            AuthException ex) {

        log.warn(
                "Authentication error: {}",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(AddingFileException.class)
    public ResponseEntity<ErrorResponse> handleAddingFileException(
            AddingFileException ex) {

        log.error(
                "File upload error",
                ex
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(DeleteFileException.class)
    public ResponseEntity<ErrorResponse> handleDeleteFileException(
            DeleteFileException ex) {

        log.error(
                "File delete error",
                ex
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(GetFileException.class)
    public ResponseEntity<ErrorResponse> handleGetFileException(
            GetFileException ex) {

        log.error(
                "File download error",
                ex
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex) {

        log.error(
                "Unexpected server error",
                ex
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
    }
}