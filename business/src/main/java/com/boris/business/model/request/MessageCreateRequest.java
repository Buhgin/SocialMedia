package com.boris.business.model.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public record MessageCreateRequest(
        @Schema(example = "1")
        @NotBlank(message = "receiverId is mandatory")
        Long receiverId,
        @Schema(example = "text")
        @NotBlank(message = "text is mandatory")
        String text) {

}
