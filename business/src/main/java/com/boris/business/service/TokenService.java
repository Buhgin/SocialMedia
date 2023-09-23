package com.boris.business.service;

import com.boris.business.mapper.request.TokenCreateMapper;
import com.boris.business.model.request.TokenCreateRequest;
import com.boris.dao.entity.Token;
import com.boris.dao.entity.User;
import com.boris.dao.enums.TokenType;
import com.boris.dao.repository.TokenRepository;
import com.boris.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final TokenCreateMapper tokenCreateMapper;
    private final UserRepository userRepository;

    public void saveUserToken(TokenCreateRequest tokenCreateRequest) {
        log.info("Creating new token: {}", tokenCreateRequest.token());
        Optional<User> user = userRepository.findByEmail(tokenCreateRequest.authDetailsDto().email());
        if(  user.isPresent()) {
             Token token = tokenCreateMapper.toEntity(tokenCreateRequest);
             token.setUser(user.get());
             token.setToken(tokenCreateRequest.token());
             token.setTokenType(TokenType.BEARER);
             token.setExpired(false);
             token.setRevoked(false);
             tokenRepository.save(token);
             log.info("Token with value='{}' created and assigned", tokenCreateRequest.token());
         }
         else {
             log.info("User with email '{}' not found", tokenCreateRequest.authDetailsDto().email());
             throw new NoSuchElementException("User not found");
         }
    }

    public void revokeAllUserTokens(TokenCreateRequest tokenCreateRequest) {
        log.info("Searching for user email '{}' and token '{}'", tokenCreateRequest.authDetailsDto().email(), tokenCreateRequest.token());
        Optional<User> user = userRepository.findByEmail(tokenCreateRequest.authDetailsDto().email());
        if(user.isEmpty()) {
            log.info("User with email '{}' not found", tokenCreateRequest.authDetailsDto().email());
            throw new NoSuchElementException("User not found");
        }
        List<Token> validUserTokens  = tokenRepository.findAllValidTokenByUser(user.get().getId());
        if(validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public Optional<Token> getOneByToken(String token) {
        log.info("Searching for stored token '{}'", token);
        var storedToken = tokenRepository.findByToken(token).orElse(null);
        if(storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            return Optional.of(storedToken);
        }
        return Optional.empty();
    }
    public List<Token> getAllValidTokenByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NoSuchElementException("User not found"));
        log.info("Searching for all valid tokens for user with id '{}'",user.getId());
        return tokenRepository.findAllValidTokenByUser(user.getId());
    }
}
