package com.boris.business.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Access and refresh token response object")
public record TokenResponse(
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbWlya2VuZXNiYXkzQGdtYWlsLmNvbSIsImlhdCI6MTY4MDcyMDUwNSwiZXhwIjoxNjgwNzIwNTUwfQ.SE8X9HlFKpg2eme-5qylmcj51u686UH-sqZTK3atSyE")
        String accessToken,
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbWlya2VuZXNiYXkzQGdtYWlsLmNvbSIsImlhdCI6MTY4MDcyMDUwNSwiZXhwIjoxNjgwNzIwNTUwfQ.SE8X9HlFKpg2eme-5qylmcj51u686UH-sqZTK3atSyE")
        String refreshToken
) {
}
