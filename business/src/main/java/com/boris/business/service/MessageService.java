package com.boris.business.service;

import com.boris.business.exception.ResourceNotFoundException;
import com.boris.business.mapper.dto.MessageMapper;
import com.boris.business.mapper.request.MessageCreateMapper;
import com.boris.business.model.dto.MessageDto;
import com.boris.business.model.enums.sort.MessageSortBy;
import com.boris.business.model.enums.sort.SortType;
import com.boris.business.model.request.MessageCreateRequest;
import com.boris.dao.entity.FriendRequest;
import com.boris.dao.entity.Message;
import com.boris.dao.entity.User;
import com.boris.dao.enums.RequestStatus;
import com.boris.dao.repository.FriendRepository;
import com.boris.dao.repository.MessageRepository;
import com.boris.dao.repository.UserRepository;
import com.boris.dao.repository.specifications.MessageSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
private final UserRepository userRepository;
private final MessageRepository messageRepository;
private final FriendRepository friendRepository;
private final MessageMapper messageMapper;
private final MessageCreateMapper messageCreateMapper;

public MessageDto createMessage(MessageCreateRequest messageCreateRequest, String username) {
        Optional<FriendRequest> friendStatus = friendRepository.findBySenderIdAndReceiverIdAndStatus(
                getUser(username).getId(),
                messageCreateRequest.receiverId(),
                RequestStatus.ACCEPTED
        );
        if (friendStatus.isPresent()) {
                Message message = messageCreateMapper.toEntity(messageCreateRequest);
                message.setSender(getUser(username));
                message.setReceiver(friendStatus.get().getReceiver());
                message.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                messageRepository.save(message);
                log.info("Creating new message with text = '{}' and the receiverId = '{}' for the user = '{}', ",messageCreateRequest.text(),messageCreateRequest.receiverId(), friendStatus.get().getReceiver().getUsername());
                return messageMapper.toDto(message);
        }
        log.error("The user = '{}' does not have a friend id = '{}'", username, messageCreateRequest.receiverId());
      throw new RuntimeException("The user = " + username + " does not have a friend with id = " + messageCreateRequest.receiverId());
}
public List<MessageDto> getChatMessage(String username,
                                       Long receiverFriendId,
                                       int pageNo,
                                       int pageSize,
                                       SortType sortType,
                                       MessageSortBy messageSortBy) {
        User senderUser = getUser(username);
        Sort sort = Sort.by(sortType.getDirection(), messageSortBy.getAttribute());
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Message> messages = messageRepository.findAll(
                MessageSpecifications.findBySenderIdAndReceiverId(senderUser.getId(), receiverFriendId),
                pageable);
        log.info("Getting chat messages for the user = '{}'", senderUser.getUsername());
        return messageMapper.toDtoList(messages.getContent());
}
        private User getUser(String userName){
                return  userRepository.findByEmail(userName).orElseThrow(() ->{
                        log.error("User not found name = '{}'", userName);
                        throw new ResourceNotFoundException("User not found name "+ userName);
                });
        }
        }