package com.boris.business.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


@Schema(description = "For creating only post. It is assumed, that company and social statuses are already defined.")
public record PostCreateRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Post title")
        String title,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Post description")
        String description,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "C:\\Users\\User\\IdeaProjects\\SocialMedia\\images")
        String image,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Post content")
        String content) {


}
