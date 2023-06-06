package com.boris.business.service;

import com.boris.business.mapper.request.TokenCreateMapper;
import com.boris.business.model.dto.AuthDetailsDto;
import com.boris.business.model.request.TokenCreateRequest;
import com.boris.dao.entity.Token;
import com.boris.dao.entity.User;
import com.boris.dao.enums.TokenType;
import com.boris.dao.repository.TokenRepository;
import com.boris.dao.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class TokenServiceTest {

    private TokenCreateRequest tokenCreateRequest;
    private User user;
    private Token token;
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private TokenCreateMapper tokenCreateMapper;

    @InjectMocks
    private TokenService tokenService;
    @Captor
    private ArgumentCaptor<List<Token>> tokenCaptors;


    @Captor
    private ArgumentCaptor<Token> tokenCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenCreateRequest = new TokenCreateRequest("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbWlya2VuZXNiYXkzQGdtYWlsLmNvbSIsImlhdCI6MTY4MDcyMDUwNSwiZXhwIjoxNjgwNzIwNTUwfQ.SE8X9HlFKpg2eme-5qylmcj51u686UH-sqZTK3atSyE",
                new AuthDetailsDto("test@example.com", "password")
        );
        user = new User();
        user.setId(123L);
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$G9XLXww0J9opgJ7erG1vp.VS4Zlnyr/gE9U7D52GYF11ngT2wWi3O");
        token = new Token();
        token.setUser(user);
        token.setToken(tokenCreateRequest.token());
        token.setTokenType(TokenType.BEARER);
        token.setExpired(false);
        token.setRevoked(false);
        token.setToken(tokenCreateRequest.token());
    }


    @Test
    public void saveUserToken() {

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(tokenCreateMapper.toEntity(any(TokenCreateRequest.class))).thenReturn(token);

        tokenService.saveUserToken(tokenCreateRequest);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(tokenCreateMapper, times(1)).toEntity(any(TokenCreateRequest.class));
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());

        Token savedToken = tokenCaptor.getValue();
        assertEquals(tokenCreateRequest.token(), savedToken.getToken());
        assertFalse(savedToken.isExpired());
        assertFalse(savedToken.isRevoked());
        assertEquals(TokenType.BEARER, savedToken.getTokenType());
    }

    @Test
    public void saveUserToken_whenUserNotFound() {

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            tokenService.saveUserToken(tokenCreateRequest);
        });
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(tokenCreateMapper, times(0)).toEntity(any(TokenCreateRequest.class));
        verify(tokenRepository, times(0)).save(any(Token.class));
    }


    @Test
    public void revokeAllUserTokens() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(token);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(tokenRepository.findAllValidTokenByUser(anyLong())).thenReturn(tokens);
        when(tokenRepository.saveAll(anyList())).thenReturn(tokens);
        tokenService.revokeAllUserTokens(tokenCreateRequest);
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(tokenRepository, times(1)).findAllValidTokenByUser(anyLong());
        verify(tokenRepository, times(1)).saveAll(tokenCaptors.capture());

        List<Token> savedTokens = tokenCaptors.getValue();
        assertTrue(savedTokens.stream().allMatch(Token::isExpired));
        assertTrue(savedTokens.stream().allMatch(Token::isRevoked));
    }

    @Test
    public void revokeAllUserTokens_whenUserNotFound() {

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            tokenService.revokeAllUserTokens(tokenCreateRequest);
        });
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(tokenRepository, times(0)).findAllValidTokenByUser(anyLong());
        verify(tokenRepository, times(0)).saveAll(anyList());
    }

}