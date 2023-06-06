package com.boris.business.service;

import com.boris.business.mapper.dto.FriendRequestMapper;
import com.boris.business.model.dto.FriendRequestDto;
import com.boris.business.model.dto.UserDto;
import com.boris.dao.entity.FriendRequest;
import com.boris.dao.entity.User;
import com.boris.dao.enums.RequestStatus;
import com.boris.dao.repository.FriendRepository;
import com.boris.dao.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FriendServiceTest {
    @Mock
    private FriendRepository friendRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendRequestMapper friendRequestMapper;
    @InjectMocks
    private FriendService friendService;

    private User userSender;
    private User userReceiver;
    private FriendRequest friendRequest,friendRequestAccepted;
    private FriendRequestDto friendRequestDto, friendRequestDtoAccepted;
    private UserDto userDtoSender, userDtoReceiver;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        friendService = new FriendService(friendRepository, userRepository, friendRequestMapper);

        userSender = new User();
        userSender.setId(1L);
        userSender.setUsername("sender");

        userReceiver = new User();
        userReceiver.setId(2L);
        userReceiver.setUsername("receiver");
        userDtoSender = new UserDto(userSender.getUsername(), userSender.getEmail());
        userDtoReceiver = new UserDto(userReceiver.getUsername(), userReceiver.getEmail());

        friendRequest = new FriendRequest();
        friendRequest.setSender(userSender);
        friendRequest.setReceiver(userReceiver);
        friendRequest.setStatus(RequestStatus.PENDING);
        friendRequestAccepted = new FriendRequest();
        friendRequestAccepted.setSender(userSender);
        friendRequestAccepted.setReceiver(userReceiver);
        friendRequestAccepted.setStatus(RequestStatus.ACCEPTED);
        friendRequestDtoAccepted = new FriendRequestDto(userDtoSender,userDtoReceiver, RequestStatus.ACCEPTED);


        friendRequestDto = new FriendRequestDto(userDtoSender,userDtoReceiver, RequestStatus.PENDING);



        when(userRepository.findByEmail(userSender.getEmail())).thenReturn(Optional.of(userSender));
        when(userRepository.findByEmail(userReceiver.getEmail())).thenReturn(Optional.of(userReceiver));
        when(userRepository.findById(userReceiver.getId())).thenReturn(Optional.of(userReceiver));


        when(friendRepository.existsBySenderIdAndReceiverId(userSender.getId(), userReceiver.getId())).thenReturn(false);
        when(friendRepository.findBySenderIdAndReceiverId(userSender.getId(), userReceiver.getId())).thenReturn(Optional.of(friendRequest));
        when(friendRepository.findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.PENDING)).thenReturn(List.of(friendRequest));
        when(friendRepository.findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.ACCEPTED)).thenReturn(List.of(friendRequestAccepted));


        when(friendRequestMapper.toDto(friendRequest)).thenReturn(friendRequestDto);
        when(friendRequestMapper.toDto(friendRequestAccepted)).thenReturn(friendRequestDtoAccepted);
    }
    @Test
    public void createFriendRequest() {
         when(userRepository.findByEmail(userSender.getEmail())).thenReturn(Optional.of(userSender));

        FriendRequestDto actual = friendService.createFriendRequest(userSender.getEmail(), userReceiver.getId());

        Assertions.assertEquals(friendRequestDto, actual);
        verify(friendRepository, times(1)).save(any(FriendRequest.class));
        verify(friendRepository, times(1)).findBySenderIdAndReceiverId(userSender.getId(), userReceiver.getId());

    }
    @Test
    public void createFriendRequestFriendRequestAlreadyExistsTest() {
        when(userRepository.findByEmail(userSender.getEmail())).thenReturn(Optional.of(userSender));

        when(friendRepository.existsBySenderIdAndReceiverId(userSender.getId(), userReceiver.getId())).thenReturn(true);

        FriendRequestDto actual = friendService.createFriendRequest(userSender.getEmail(), userReceiver.getId());

        Assertions.assertEquals(friendRequestDto, actual);
        verify(friendRepository, times(0)).save(any(FriendRequest.class));
        verify(friendRepository, times(1)).findBySenderIdAndReceiverId(userSender.getId(), userReceiver.getId());

    }
    @Test
    public void createFriendRequestWhenUserIsNotFoundTest() {
        Long nonExistentId = 3L;

        Exception exception = assertThrows(RuntimeException.class, () ->
                friendService.createFriendRequest(userSender.getEmail(), nonExistentId));

        Assertions.assertEquals("User not found" + nonExistentId, exception.getMessage());
    }

    @Test
    public void getFriendshipNotifications() {
        Set<FriendRequestDto> actual = friendService.getFriendshipNotifications(userReceiver.getEmail());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertTrue(actual.contains(friendRequestDto));
        verify(friendRepository, times(1)).findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.PENDING);
        verify(friendRequestMapper, times(1)).toDto(friendRequest);
    }
    @Test
    public void getFriendshipNotificationsDifferentStatusTest() {
        Set<FriendRequestDto> actual = friendService.getFriendshipNotifications(userReceiver.getEmail());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertTrue(actual.contains(friendRequestDto));
        Assertions.assertFalse(actual.contains(friendRequestDtoAccepted));
        verify(friendRepository, times(1)).findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.PENDING);
        verify(friendRequestMapper, times(1)).toDto(friendRequest);
    }


    @Test
    public void getAllFriends() {

        Set<FriendRequestDto> actual = friendService.getAllFriends(userReceiver.getEmail());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertTrue(actual.contains(friendRequestDtoAccepted));
        verify(friendRepository, times(1)).findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.ACCEPTED);
        verify(friendRequestMapper, times(1)).toDto(friendRequestAccepted);
    }
    @Test
    public void getAllFriendsNoFriendsTest() {

        when(friendRepository.findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.ACCEPTED))
                .thenReturn(new ArrayList<>());

        Set<FriendRequestDto> actual = friendService.getAllFriends(userReceiver.getEmail());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(0, actual.size());
        verify(friendRepository, times(1)).findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.ACCEPTED);
    }

    @Test
    public void acceptFriendAllRequests() {
        when(friendRepository.findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.PENDING))
                .thenReturn(List.of(friendRequest));
        when(friendRequestMapper.toDto(friendRequest)).thenReturn(friendRequestDto);

        Set<FriendRequestDto> actual = friendService.acceptFriendAllRequests(userReceiver.getEmail());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertTrue(actual.contains(friendRequestDto));
        Assertions.assertEquals(RequestStatus.ACCEPTED, friendRequest.getStatus());
        verify(friendRepository, times(1)).findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.PENDING);
        verify(friendRepository, times(1)).save(friendRequest);
        verify(friendRequestMapper, times(1)).toDto(friendRequest);
    }

  /*  @Test
    public void acceptFriendRequest() {
        FriendCreateRequest friendCreateRequest = new FriendCreateRequest(userReceiver.getId(), true);
        when(userRepository.findByEmail(userSender.getEmail())).thenReturn(Optional.of(userSender));
        when(friendRepository.findBySenderIdAndReceiverId(userReceiver.getId(), userSender.getId()))
                .thenReturn(Optional.of(friendRequest));
             friendRequest.setStatus(RequestStatus.ACCEPTED);

        when(friendRepository.save(friendRequest)).thenAnswer(invocation -> {
            FriendRequest fr = (FriendRequest) invocation.getArguments()[0];
            Assertions.assertEquals(RequestStatus.ACCEPTED, fr.getStatus());
             return fr;
        });

        FriendRequestDto actual = friendService.acceptFriendRequest(friendCreateRequest, userSender.getEmail());
        System.out.println(actual.status());
        Assertions.assertEquals(friendRequestDtoAccepted, actual);
        verify(friendRepository, times(2)).save(any(FriendRequest.class));
        verify(friendRequestMapper, times(1)).toDto(any(FriendRequest.class));
    }*/

    @Test
    public void deleteFriendRequest() {
    }
}