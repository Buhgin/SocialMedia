package com.boris.business.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (

        @Schema(example = "boris@ya.ru")
        @Email(message = "Email should be valid")
        String email,
        @Schema(example = "12345678")
        @NotBlank(message = "Password is mandatory")
        String password
){

}
