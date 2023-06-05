package com.boris.business.service;

import com.boris.business.mapper.dto.AuthDetailsMapper;
import com.boris.business.mapper.request.AuthDetailsCreateMapper;
import com.boris.business.model.dto.AuthDetailsDto;
import com.boris.business.model.request.LoginRequest;
import com.boris.business.model.request.RegistrationRequest;
import com.boris.dao.entity.User;
import com.boris.dao.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthDetailsServiceTest {
    private User user1, user2, user3;
    private AuthDetailsDto authDetailsDto1, authDetailsDto2, authDetailsDto3;
    private LoginRequest login1, login2, login3;
    private RegistrationRequest regis1, regis2, regis3;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthDetailsMapper authDetailsMapper;
    @Mock
    private AuthDetailsCreateMapper authDetailsCreateMapper;
    @InjectMocks
    private AuthDetailsService authDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = new User(1L, "user1", "user1@email.ru","password");
        user2 = new User(2L, "user2", "user2@email.ru","password");
        user3 = new User(3L, "user3", "user3@email.ru","password");
        authDetailsDto1 = new AuthDetailsDto("user1@email.ru", "password");
        authDetailsDto2 = new AuthDetailsDto( "user2@email.ru", "password");
        authDetailsDto3 = new AuthDetailsDto("user3@email.ru", "password");
        login1 = new LoginRequest("user1@email.ru", "password");
        login2 = new LoginRequest("user2@email.ru", "password");
        login3 = new LoginRequest("user3@email.ru", "password");
        regis1 = new RegistrationRequest("user1","user1@email.ru" ,"password");
        regis2 = new RegistrationRequest("user2", "user2@email.ru" ,"password");
        regis3 = new RegistrationRequest("user3", "user3@email.ru" ,"password");

        when(authDetailsCreateMapper.toEntity(regis1)).thenReturn(user1);
        when(authDetailsCreateMapper.toEntity(regis2)).thenReturn(user2);
        when(authDetailsCreateMapper.toEntity(regis3)).thenReturn(user3);
        when(authDetailsMapper.toDto(user1)).thenReturn(authDetailsDto1);
        when(authDetailsMapper.toDto(user2)).thenReturn(authDetailsDto2);
        when(authDetailsMapper.toDto(user3)).thenReturn(authDetailsDto3);
        when(userRepository.findByUsernameOrEmail(login1.email(),login1.email())).thenReturn(Optional.of(user1));
        when(userRepository.findByUsernameOrEmail(login2.email(),login2.email())).thenReturn(Optional.of(user2));
        when(userRepository.findByUsernameOrEmail(login3.email(),login3.email())).thenReturn(Optional.of(user3));
        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);
        when(userRepository.save(user3)).thenReturn(user3);
        when(authDetailsMapper.toDto(user1)).thenReturn(authDetailsDto1);
        when(authDetailsMapper.toDto(user2)).thenReturn(authDetailsDto2);
        when(authDetailsMapper.toDto(user3)).thenReturn(authDetailsDto3);
    }

    @Test
    public void getOneByEmail() {
        assertEquals(authDetailsDto1, authDetailsService.getOneByEmail(login1.email()));
        assertEquals(authDetailsDto2, authDetailsService.getOneByEmail(login2.email()));
        assertEquals(authDetailsDto3, authDetailsService.getOneByEmail(login3.email()));
    }

    @Test
    public void create() {
        assertEquals(authDetailsDto1, authDetailsService.create(regis1));
        assertEquals(authDetailsDto2, authDetailsService.create(regis2));
        assertEquals(authDetailsDto3, authDetailsService.create(regis3));
    }
}