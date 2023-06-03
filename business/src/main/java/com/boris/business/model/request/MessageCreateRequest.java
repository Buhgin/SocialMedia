package com.boris.business.model.request;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MessageCreateRequest(
        @Schema(example = "text")
        String text,
        @Schema(example = "2021-01-01T00:00:00")
        LocalDateTime createdAt) {

}
