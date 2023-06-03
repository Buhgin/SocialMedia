package com.boris.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {
    private ApiResponse() {
        throw new IllegalStateException("Util class");
    }

    public static ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(ApiErrorResponse.builder()
                        .httpStatus(status)
                        .message(message)
                        .build());

    }
}
