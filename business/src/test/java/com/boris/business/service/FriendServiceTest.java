package com.boris.business.service;

import com.boris.business.mapper.dto.FriendRequestMapper;
import com.boris.business.model.dto.FriendRequestDto;
import com.boris.business.model.dto.UserDto;
import com.boris.business.model.request.FriendCreateRequest;
import com.boris.dao.entity.FriendRequest;
import com.boris.dao.entity.User;
import com.boris.dao.enums.RequestStatus;
import com.boris.dao.repository.FriendRepository;
import com.boris.dao.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class FriendServiceTest {
    @Mock
    private FriendRepository friendRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendRequestMapper friendRequestMapper;
    @Captor
    private ArgumentCaptor<FriendRequest> friendRequestArgumentCaptor;
    @InjectMocks
    private FriendService friendService;

    private User userSender, userReceiver;

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
        userSender.setEmail("sender@email.ru");

        userReceiver = new User();
        userReceiver.setId(2L);
        userReceiver.setUsername("receiver");
        userReceiver.setEmail("receiver@email.ru");
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

    }
    @Test
    public void getFriendshipNotificationsDifferentStatusTest() {
        Set<FriendRequestDto> actual = friendService.getFriendshipNotifications(userReceiver.getEmail());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertTrue(actual.contains(friendRequestDto));
        Assertions.assertFalse(actual.contains(friendRequestDtoAccepted));
        verify(friendRepository, times(1)).findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.PENDING);

    }


    @Test
    public void getAllFriends() {

        Set<FriendRequestDto> actual = friendService.getAllFriends(userReceiver.getEmail());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertTrue(actual.contains(friendRequestDtoAccepted));
        verify(friendRepository, times(1)).findAllByReceiverIdAndStatus(userReceiver.getId(), RequestStatus.ACCEPTED);
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

    }

    @Test
    public void acceptFriendRequest() {

        Long receiverId = friendRequest.getReceiver().getId();
        Long senderId = userSender.getId();
        FriendCreateRequest friendCreateRequest = new FriendCreateRequest(receiverId, true);

        when(userRepository.findByEmail(userSender.getEmail())).thenReturn(Optional.of(userSender));
        when(friendRepository.findBySenderIdAndReceiverId(receiverId, senderId)).thenReturn(Optional.of(friendRequest));
        when(friendRepository.findBySenderIdAndReceiverId(senderId, receiverId)).thenReturn(Optional.of(friendRequest));
        when(friendRequestMapper.toDto(any(FriendRequest.class))).thenAnswer(invocation -> {
            FriendRequest argumentFriendRequest = (FriendRequest) invocation.getArgument(0);
            if (argumentFriendRequest.getStatus() == RequestStatus.ACCEPTED) {
                return friendRequestDtoAccepted;
            } else {
                return friendRequestDto;
            }
        });

        FriendRequestDto actual = friendService.acceptFriendRequest(friendCreateRequest, userSender.getEmail());

        verify(friendRepository, times(2)).save(friendRequestArgumentCaptor.capture());
        FriendRequest savedFriendRequest1 = friendRequestArgumentCaptor.getAllValues().get(0);
        FriendRequest savedFriendRequest2 = friendRequestArgumentCaptor.getAllValues().get(1);

        Assertions.assertEquals(friendRequestDtoAccepted, actual);
        Assertions.assertEquals(RequestStatus.ACCEPTED, savedFriendRequest1.getStatus());
        Assertions.assertEquals(RequestStatus.ACCEPTED, savedFriendRequest2.getStatus());
        Assertions.assertEquals(receiverId, savedFriendRequest1.getReceiver().getId());
        Assertions.assertEquals(senderId, savedFriendRequest1.getSender().getId());

    }
    @Test
    public void acceptFriendRequestWithNonExistingFriendRequest() {

        Long receiverId = friendRequest.getReceiver().getId();
        Long senderId = userSender.getId();
        FriendCreateRequest friendCreateRequest = new FriendCreateRequest(receiverId, true);

        when(userRepository.findByEmail(userSender.getEmail())).thenReturn(Optional.of(userSender));
        when(friendRepository.findBySenderIdAndReceiverId(receiverId, senderId)).thenReturn(Optional.of(friendRequest));
        when(friendRepository.findBySenderIdAndReceiverId(senderId, receiverId)).thenReturn(Optional.empty());
        when(friendRequestMapper.toDto(any(FriendRequest.class))).thenAnswer(invocation -> {
            FriendRequest argumentFriendRequest = (FriendRequest) invocation.getArgument(0);
            if (argumentFriendRequest.getStatus() == RequestStatus.ACCEPTED) {
                return friendRequestDtoAccepted;
            } else {
                return friendRequestDto;
            }
        });
        FriendRequestDto actual = friendService.acceptFriendRequest(friendCreateRequest, userSender.getEmail());


        verify(friendRepository, times(2)).save(friendRequestArgumentCaptor.capture());
        List<FriendRequest> capturedFriendRequests = friendRequestArgumentCaptor.getAllValues();
        FriendRequest savedFriendRequest1 = capturedFriendRequests.get(0);
        FriendRequest savedFriendRequest2 = capturedFriendRequests.get(1);

        Assertions.assertEquals(RequestStatus.ACCEPTED, savedFriendRequest1.getStatus());
        Assertions.assertEquals(RequestStatus.ACCEPTED, savedFriendRequest2.getStatus());
        Assertions.assertEquals(receiverId, savedFriendRequest1.getReceiver().getId());
        Assertions.assertEquals(senderId, savedFriendRequest1.getSender().getId());
        Assertions.assertEquals(friendRequestDtoAccepted, actual);
    }
    @Test
    public void deleteFriendRequestWithNonExistingFriendRequest() {

        Long receiverId = friendRequest.getReceiver().getId();
        Long senderId = userSender.getId();

        when(userRepository.findByEmail(userSender.getEmail())).thenReturn(Optional.of(userSender));
        when(friendRepository.findBySenderIdAndReceiverId(senderId, receiverId)).thenReturn(Optional.of(friendRequest));
        when(friendRepository.findBySenderIdAndReceiverId(receiverId, senderId)).thenReturn(Optional.empty()); // Existing request is not present

        friendService.deleteFriendRequest(receiverId, userSender.getEmail());

        verify(friendRepository, times(1)).delete(friendRequestArgumentCaptor.capture());
        FriendRequest deletedFriendRequest = friendRequestArgumentCaptor.getValue();

        Assertions.assertEquals(receiverId, deletedFriendRequest.getReceiver().getId());
        Assertions.assertEquals(senderId, deletedFriendRequest.getSender().getId());
    }
    @Test
    public void deleteFriendRequest() {

            Long receiverId = friendRequest.getReceiver().getId();
            Long senderId = userSender.getId();

            when(userRepository.findByEmail(userSender.getEmail())).thenReturn(Optional.of(userSender));
            when(friendRepository.findBySenderIdAndReceiverId(senderId, receiverId)).thenReturn(Optional.of(friendRequest));
            when(friendRepository.findBySenderIdAndReceiverId(receiverId, senderId)).thenReturn(Optional.of(friendRequest));

            friendService.deleteFriendRequest(receiverId, userSender.getEmail());

        verify(friendRepository, times(1)).save(friendRequestArgumentCaptor.capture());
        verify(friendRepository, times(1)).delete(friendRequestArgumentCaptor.capture());
        FriendRequest updatedFriendRequest = friendRequestArgumentCaptor.getAllValues().get(0);
        FriendRequest deletedFriendRequest = friendRequestArgumentCaptor.getAllValues().get(1);

        Assertions.assertEquals(RequestStatus.DECLINED, updatedFriendRequest.getStatus());
        Assertions.assertEquals(receiverId, deletedFriendRequest.getReceiver().getId());
        Assertions.assertEquals(senderId, deletedFriendRequest.getSender().getId());

    }
}