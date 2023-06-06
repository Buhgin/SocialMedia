package com.boris.business.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Object with full post data")
public record PostDto(
        @Schema(example = "1")
        Long id,

        @Schema(example = "Post title")
        String title,

        @Schema(example = "Post description")
        String description,
        @Schema(example = "C:\\Users\\User\\IdeaProjects\\SocialMedia\\images")
        String image,
        @Schema(example = "username boris, email boris@ya.ru")
        UserDto user,

        @Schema(example = "2021-08-01T12:00:00")
        LocalDateTime createdAt) {
}
