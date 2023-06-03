package com.boris.business.service;

import com.boris.business.mapper.dto.AuthDetailsMapper;
import com.boris.dao.entity.Message;
import com.boris.dao.repository.FriendRepository;
import com.boris.dao.repository.MessageRepository;
import com.boris.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
private final UserRepository userRepository;
private final MessageRepository messageRepository;
private final FriendRepository friendRepository;

public void createMessage(Message message) {
        messageRepository.save(message);
        }
}
