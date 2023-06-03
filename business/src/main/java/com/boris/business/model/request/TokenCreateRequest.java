package com.boris.business.model.request;

import com.boris.business.model.dto.AuthDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


public record TokenCreateRequest(
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbWlya2VuZXNiYXkzQGdtYWlsLmNvbSIsImlhdCI6MTY4MDcyMDUwNSwiZXhwIjoxNjgwNzIwNTUwfQ.SE8X9HlFKpg2eme-5qylmcj51u686UH-sqZTK3atSyE")
        String token,
        @Schema(example = "authDetailsDto")
        AuthDetailsDto authDetailsDto) {

}
