package com.boris.business.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDto(
        @Schema(example = "boris")
        String username,
        @Schema(example = "boris@email.ru")
        String email) {
}
