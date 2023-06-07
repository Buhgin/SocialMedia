package com.boris.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
@Schema(description = "Provides more specific information about error occurred. " +
        "Messages can be localized to show the user the reason of operation failure")
public record ApiErrorResponse(
        @Schema(example = "CONFLICT")
        HttpStatus httpStatus,
        @Schema(example = "Entity not found id")
        String message) {
}