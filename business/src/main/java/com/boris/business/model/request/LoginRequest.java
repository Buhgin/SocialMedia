package com.boris.business.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
public record LoginRequest (
        @Schema(example = "Boris")
        String username,
        @Schema(example = "boris@ya.ru")
        String email,
        @Schema(example = "12345678")
        String password
){

}
