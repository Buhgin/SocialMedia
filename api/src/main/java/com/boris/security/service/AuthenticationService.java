package com.boris.security.service;

import com.boris.business.exception.ResourceNotFoundException;
import com.boris.business.model.dto.AuthDetailsDto;
import com.boris.business.model.request.AccessTokenCreateRequest;
import com.boris.business.model.request.LoginRequest;
import com.boris.business.model.request.RegistrationRequest;
import com.boris.business.model.request.TokenCreateRequest;
import com.boris.business.model.response.AccessTokenResponse;
import com.boris.business.model.response.TokenResponse;
import com.boris.business.service.AuthDetailsService;
import com.boris.business.service.TokenService;
import com.boris.dao.entity.Token;
import com.boris.dao.repository.TokenRepository;
import com.boris.security.config.filter.JwtService;
import com.boris.security.details.AuthenticationDetails;
import com.boris.util.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthDetailsService authDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationDetails authenticationDetails;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public TokenResponse register(RegistrationRequest registrationRequest) {
        RegistrationRequest saveRequest = new RegistrationRequest(registrationRequest.username(),
                registrationRequest.email(),
               passwordEncoder.encode(registrationRequest.password())
        );

        AuthDetailsDto authDetailsDto = authDetailsService.create(saveRequest);
        authenticationDetails.setAuthDetailsDto(authDetailsDto);

        String jwtAccessToken = jwtService.generateAccessToken(authenticationDetails);
        String jwtRefreshToken = jwtService.generateRefreshToken(authenticationDetails);

        TokenCreateRequest tokenCreateRequest = getToken(jwtAccessToken, authDetailsDto);

        tokenService.saveUserToken(tokenCreateRequest);

        return setTokens(jwtAccessToken, jwtRefreshToken);
    }

    public TokenResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        AuthDetailsDto authDetailsDto = authDetailsService.getOneByEmail(loginRequest.email());
        authenticationDetails.setAuthDetailsDto(authDetailsDto);

        var jwtAccessToken = jwtService.generateAccessToken(authenticationDetails);
        var jwtRefreshToken = jwtService.generateRefreshToken(authenticationDetails);

        TokenCreateRequest tokenCreateRequest = getToken(jwtAccessToken, authDetailsDto);

        tokenService.revokeAllUserTokens(tokenCreateRequest);

        tokenService.saveUserToken(tokenCreateRequest);

        return setTokens(jwtAccessToken, jwtRefreshToken);
    }

    public AccessTokenResponse refresh(AccessTokenCreateRequest accessTokenCreateRequest) {
        String extractedEmailFromRefreshTokenClaim = jwtService.extractUsername(accessTokenCreateRequest.refreshToken());

        AuthDetailsDto authDetailsDto = authDetailsService.getOneByEmail(extractedEmailFromRefreshTokenClaim);
        authenticationDetails.setAuthDetailsDto(authDetailsDto);

        var jwtAccessToken = jwtService.generateAccessToken(authenticationDetails);

        TokenCreateRequest tokenCreateRequest = getToken(jwtAccessToken, authDetailsDto);

        tokenService.revokeAllUserTokens(tokenCreateRequest);
        tokenService.saveUserToken(tokenCreateRequest);

        return AccessTokenResponse.builder()
                .accessToken(jwtAccessToken)
                .build();
    }
    public String logoutUser(String UserName) {
        List<Token> allValidTokenByUser = tokenService.getAllValidTokenByUser(UserName);
        if(allValidTokenByUser.size()==0){
           throw new ResourceNotFoundException("no valid tokens");
        }
        for(Token token: allValidTokenByUser){
            tokenService.getOneByToken(token.getToken());
        }
        return "ok";
    }

    private TokenResponse setTokens(String jwtAccessToken, String jwtRefreshToken) {
        return TokenResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    private TokenCreateRequest getToken(String jwtAccessToken, AuthDetailsDto authDetailsDto) {

        return new TokenCreateRequest(jwtAccessToken, authDetailsDto);
    }

}
