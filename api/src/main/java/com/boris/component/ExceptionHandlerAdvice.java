package com.boris.component;

import com.boris.business.exception.EntityNameExistsException;
import com.boris.business.exception.ResourceNotFoundException;
import com.boris.model.ApiErrorResponse;
import com.boris.model.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<ApiErrorResponse> httpMessageNotReadableHandler(HttpMessageNotReadableException exception) {
        return ApiResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<ApiErrorResponse> entityNotFoundHandler(EntityNotFoundException exception) {
        return ApiResponse.buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(EntityNameExistsException.class)
    private ResponseEntity<ApiErrorResponse> entityNameExistsHandler(EntityNameExistsException exception) {
        return ApiResponse.buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<ApiErrorResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException exception) {
        return ApiResponse.buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {

        return  ApiResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ApiResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, errors.toString().replaceAll("^\\[|\\]$", ""));
    }
}
