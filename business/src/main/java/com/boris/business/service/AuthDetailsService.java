package com.boris.business.service;

import com.boris.business.exception.UserAlreadyExistsException;
import com.boris.business.mapper.dto.AuthDetailsMapper;
import com.boris.business.mapper.request.AuthDetailsCreateMapper;
import com.boris.business.model.dto.AuthDetailsDto;
import com.boris.business.model.request.RegistrationRequest;
import com.boris.dao.entity.User;
import com.boris.dao.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthDetailsService {
    private final UserRepository userRepository;
    private final AuthDetailsMapper authDetailsMapper;
    private final AuthDetailsCreateMapper authDetailsCreateMapper;

    public AuthDetailsDto getOneByEmail(String login) {
        log.info("Searching for email='{}'", login);
        return authDetailsMapper.toDto(userRepository.findByUsernameOrEmail(login,login)
                .orElseThrow(() -> new EntityNotFoundException("Email not found")));
    }

    public AuthDetailsDto create(RegistrationRequest request) {
        log.info("Creating new user with email='{}'", request.email());
        User user = authDetailsCreateMapper.toEntity(request);
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.info("User with email {} already exists", user.getEmail());
            throw new UserAlreadyExistsException("User with this email already exists email = "
                    + user.getEmail());
        }
        userRepository.save(user);

        log.info("User with email {} created and id={} assigned", user.getEmail(), user.getId());
        return authDetailsMapper.toDto(user);
    }

}
