package com.boris.business.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MessageDto(

        @Schema(example = "User sender name: Ivan, email: ivan@email.ru ")
        UserDto sender,
        @Schema(example = "Hello, how are you?")
        String text,
        @Schema(example = "User receiver name: Boris, email: boris@email.ru ")
        UserDto receiver,
        @Schema(example = "2021-08-01T12:00:00")
        LocalDateTime createdAt) {
}
