package com.boris.business.service;

import com.boris.business.exception.ResourceNotFoundException;
import com.boris.business.mapper.dto.MessageMapper;
import com.boris.business.mapper.request.MessageCreateMapper;
import com.boris.business.model.dto.MessageDto;
import com.boris.business.model.dto.UserDto;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MessageServiceTest {
    private User user;
    private Message message;
    private UserDto userDto;
    private MessageDto messageDto;
    private String userName;
    private int pageNo;
    private int pageSize;
    private SortType sortType;
    private MessageSortBy messageSortBy;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageCreateMapper messageCreateMapper;
    @Mock
    private MessageCreateRequest messageCreateRequest;

    @Mock
    private MessageMapper messageMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private MessageService messageService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pageNo = 0;
        pageSize = 10;
        sortType = SortType.DESC;
        messageSortBy = MessageSortBy.CREATED_AT;
        user = mock(User.class);
        userName = "testUser";
        message = mock(Message.class);
        messageCreateRequest = new MessageCreateRequest(1L, "test message");
        message.setSender(user);
        message.setId(1L);
        messageDto = new MessageDto( new UserDto(userName, "test@ya.ru"),"text test",
                (new UserDto("receiver","receiver@ya.ru")),LocalDateTime.now());

        when(messageCreateMapper.toEntity(messageCreateRequest)).thenReturn(message);
        when(messageMapper.toDto(message)).thenReturn(messageDto);


    }

    @Test
    public void createMessage_shouldCreateMessageSuccessfully_whenUserExistsAndFriendStatusIsPresent() {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(1L, "test message");
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setReceiver(user);
        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        when(friendRepository.findBySenderIdAndReceiverIdAndStatus(user.getId(), messageCreateRequest.receiverId(), RequestStatus.ACCEPTED)).thenReturn(Optional.of(friendRequest));
        when(messageMapper.toDto(any(Message.class))).thenAnswer(invocation -> {
            Message messageArg = invocation.getArgument(0);
            if (messageArg == message) {
                return messageDto;
            } else {
                return null;
            }
        });
        MessageDto actualMessageDto = messageService.createMessage(messageCreateRequest, userName);

        verify(messageRepository, times(1)).save(any(Message.class));
        Assertions.assertNotNull(messageDto);
        Assertions.assertEquals(messageDto, actualMessageDto);
    }

    @Test
    public void createMessage_shouldThrowResourceNotFoundException_whenUserNotFound() {

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(1L, "test message");
        when(userRepository.findByEmail(userName)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                messageService.createMessage(messageCreateRequest, userName));

        String expectedMessage = "User not found name " + userName;
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(messageRepository, times(0)).save(any(Message.class));
    }

    @Test
    public void createMessage_shouldThrowRuntimeException_whenFriendStatusIsNotPresent() {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(1L, "test message");
        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        when(friendRepository.findBySenderIdAndReceiverIdAndStatus(user.getId(), messageCreateRequest.receiverId(), RequestStatus.ACCEPTED)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                messageService.createMessage(messageCreateRequest, userName));

        String expectedMessage = "The user " + userName + " does not have a friend with id " + messageCreateRequest.receiverId();
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(messageRepository, times(0)).save(any(Message.class));
    }

    @Test
    public void getChatMessage() {
        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        Sort sort = Sort.by(sortType.getDirection(), messageSortBy.getAttribute());
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Message> messages = new PageImpl<>(List.of(message));
        when(messageRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(messages);
        when(messageMapper.toDtoList(anyList())).thenReturn(List.of(messageDto));

        List<MessageDto> messageDtos = messageService.getChatMessage(userName, 1L, pageNo, pageSize, sortType, messageSortBy);

        verify(messageRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        Assertions.assertNotNull(messageDtos);
        Assertions.assertFalse(messageDtos.isEmpty());
    }

    @Test
    public void getChatMessage_shouldThrowResourceNotFoundException_whenUserIsNotFound() {
        when(userRepository.findByEmail(userName)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                messageService.getChatMessage(userName, 1L, pageNo, pageSize, sortType, messageSortBy));

        String expectedMessage = "User not found name " + userName;
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}