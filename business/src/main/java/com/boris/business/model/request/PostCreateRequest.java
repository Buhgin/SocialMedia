package com.boris.business.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "For creating only post. It is assumed, that company and social statuses are already defined.")
public record PostCreateRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Post title")
        @NotBlank(message = "Title is mandatory")
        String title,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Post description")
        @NotBlank(message = "Description is mandatory")
        String description,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "C:\\Users\\User\\IdeaProjects\\SocialMedia\\images")
        String image,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Post content")
        String content) {


}
