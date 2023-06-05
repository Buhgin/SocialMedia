package com.boris.business.model.request;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MessageCreateRequest(
        @Schema(example = "1")
        Long receiverId,
        @Schema(example = "text")
        String text) {

}
