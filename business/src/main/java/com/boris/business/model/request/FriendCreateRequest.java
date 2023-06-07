package com.boris.business.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record FriendCreateRequest(
        @Schema(example = "1")
        @NotBlank(message = "userReceiverId is mandatory")
        Long userReceiverId,
        @Schema(example = "true")
        @NotBlank(message = "accept is mandatory")
        boolean accept
) {
}
