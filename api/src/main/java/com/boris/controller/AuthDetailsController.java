package com.boris.controller;

import com.boris.business.model.dto.PostDto;
import com.boris.business.model.request.AccessTokenCreateRequest;
import com.boris.business.model.request.LoginRequest;
import com.boris.business.model.request.RegistrationRequest;
import com.boris.business.model.response.AccessTokenResponse;
import com.boris.business.model.response.TokenResponse;
import com.boris.model.ApiErrorResponse;
import com.boris.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/${api.version}/auth")
@Tag(name = "Authorization details")
public class AuthDetailsController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New user created and returned with new access and refresh tokens", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TokenResponse.class))}),
            @ApiResponse(responseCode = "409", description = "User email is already taken", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })

    })
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(registrationRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "User authorization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is authorized and returned with new access and refresh tokens", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TokenResponse.class))}),
            @ApiResponse(responseCode = "409", description = "User's email or password incorrect", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })

    })
    public TokenResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "Refresh", description = "Refreshing access token bt refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returned new access token", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccessTokenResponse.class))}),
            @ApiResponse(responseCode = "409", description = "JWT validity cannot be asserted and should not be trusted", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })

    })
    public AccessTokenResponse refresh(@Valid @RequestBody AccessTokenCreateRequest accessTokenCreateRequest) {
        return authenticationService.refresh(accessTokenCreateRequest);
    }
}
