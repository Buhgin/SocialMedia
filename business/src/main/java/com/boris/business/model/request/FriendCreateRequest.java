package com.boris.business.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record FriendCreateRequest(
        @Schema(example = "1")
        Long userReceiverId,
        @Schema(example = "true")
        boolean accept
) {
}
