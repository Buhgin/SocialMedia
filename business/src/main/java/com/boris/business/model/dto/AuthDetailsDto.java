package com.boris.business.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthDetailsDto(
        @Schema(example = "boris@email.ru")
        String email,
        @Schema(example = "12345678")
        String password) {
}
