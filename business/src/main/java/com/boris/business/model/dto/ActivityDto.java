package com.boris.business.model.dto;

import com.boris.dao.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ActivityDto(
        @Schema(example = "1")
        Long id,
        @Schema(example = "User")
        UserDto user,
        @Schema(example = "ActivityType")
        ActivityType type,
        @Schema(example = "Post")
        PostDto post,

        @Schema(example = "2021-08-01T12:00:00")
        LocalDateTime createdAt

) {

}
