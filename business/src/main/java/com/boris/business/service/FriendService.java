package com.boris.business.service;

import com.boris.business.exception.ResourceNotFoundException;
import com.boris.business.mapper.dto.FriendRequestMapper;
import com.boris.business.model.dto.FriendRequestDto;
import com.boris.business.model.request.FriendCreateRequest;
import com.boris.dao.entity.FriendRequest;
import com.boris.dao.entity.User;
import com.boris.dao.enums.RequestStatus;
import com.boris.dao.repository.FriendRepository;
import com.boris.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendRequestMapper friendRequestMapper;

    public FriendRequestDto createFriendRequest(String nameSender,
                                                Long userReceiverId) {
        User userSender = getUser(nameSender);
        if (!friendRepository.existsBySenderIdAndReceiverId(userSender.getId(), userReceiverId) &&
                !userReceiverId.equals(userSender.getId())) {
            User userReceiver = userRepository.findById(userReceiverId).orElseThrow(() -> {
                log.error("User with id={} not found", userReceiverId);
                throw new ResourceNotFoundException("User with id=" + userReceiverId + " not found");
            });
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setSender(userSender);
            friendRequest.setReceiver(userReceiver);
            friendRequest.setStatus(RequestStatus.PENDING);
            friendRepository.save(friendRequest);
            log.info("friend request created from user id ='{}' for user id = '{}'", userSender.getId(), userReceiverId);
        }
        log.info("FriendRequest already exists userSender id ='{}' for userReceiver id = '{}'", userSender.getId(), userReceiverId);
        return friendRequestMapper.toDto(friendRepository.findBySenderIdAndReceiverId(userSender.getId(), userReceiverId).orElseThrow(() -> {
            log.error("FriendRequest user not found Id = {}", userReceiverId);
            throw new ResourceNotFoundException("FriendRequest userReceiver not found id = " + userReceiverId);
        }));
    }

    public Set<FriendRequestDto> getFriendshipNotifications(String userReceiver) {
        List<FriendRequest> allByReceiverId = friendRepository.findAllByReceiverIdAndStatus(
                getUser(userReceiver).getId(), RequestStatus.PENDING);
        if (allByReceiverId.size() != 0) {
            log.info("Friend requests with status PENDING found for userReceiver id ='{}'", getUser(userReceiver).getId());
            return allByReceiverId.stream()
                    .map(friendRequestMapper::toDto)
                    .collect(Collectors.toSet());
        }
        log.info("No friend requests");
        return new HashSet<>();
    }

    public Set<FriendRequestDto> getAllFriends(String userReceiver) {
        User user = getUser(userReceiver);
        List<FriendRequest> allByReceiverId = friendRepository.findAllByReceiverIdAndStatus(
                user.getId(), RequestStatus.ACCEPTED);
        if (allByReceiverId.size() != 0) {
            log.info("Friend requests with status ACCEPTED found for userReceiver id ='{}'", user.getId());
            return allByReceiverId.stream()
                    .map(friendRequestMapper::toDto)
                    .collect(Collectors.toSet());
        }
        log.info("No friend requests");
        return new HashSet<>();
    }

    public Set<FriendRequestDto> acceptFriendAllRequests(String receiverName) {
        User user = getUser(receiverName);
        List<FriendRequest> friendRequests = friendRepository.findAllByReceiverIdAndStatus(
                user.getId(), RequestStatus.PENDING);
        if (friendRequests.size() != 0) {
            return friendRequests.stream()
                    .peek(friendRequest -> {
                        friendRequest.setStatus( RequestStatus.ACCEPTED);
                        friendRepository.save(friendRequest);
                        createFriend(friendRequest);
                        log.info("Friend request accepted from userName ='{}'", friendRequest.getReceiver().getUsername());
                    }).map(friendRequestMapper::toDto)
                    .collect(Collectors.toSet());
        }
        log.info("No friend requests");
        return new HashSet<>();
    }

    public FriendRequestDto acceptFriendRequest(FriendCreateRequest friendCreateRequest, String senderName) {
        User user = getUser(senderName);

        FriendRequest friendRequest = friendRepository.findBySenderIdAndReceiverId(
                        friendCreateRequest.userReceiverId(), user.getId())
                .orElseThrow(() -> {
                    log.error("No friend request found for user with ID= '{}'", friendCreateRequest.userReceiverId());
                    return new ResourceNotFoundException("Not friendRequest found for user with ID= "+ friendCreateRequest.userReceiverId());
                });

        if (friendCreateRequest.accept()) {
            friendRequest.setStatus(RequestStatus.ACCEPTED);
            createFriend(friendRequest);
            friendRepository.save(friendRequest);
            log.info("Friend Request accepted by user id ='{}' from user id='{}'",
                  user.getId(),
                    friendRequest.getReceiver().getId());
        } else {
            friendRequest.setStatus(RequestStatus.DECLINED);
            friendRepository.save(friendRequest);
            log.info("Friend Request declined by user id ='{}' from user id='{}'",
                    user.getId(),
                    friendRequest.getReceiver().getId());
        }

        return friendRequestMapper.toDto(friendRequest);
    }

    public void deleteFriendRequest(Long userReceiverId, String senderName) {
        User user = getUser(senderName);
        FriendRequest friendRequest = friendRepository.findBySenderIdAndReceiverId(
                        user.getId(), userReceiverId)
                .orElseThrow(() -> {
                    log.error("No friend requests");
                    return new ResourceNotFoundException("Not friendRequest found for user with ID= "+ userReceiverId);
                });
        deleteFriend(friendRequest);
        friendRepository.delete(friendRequest);
        log.info("Friend request deleted by user id ='{}' from user id='{}'",
                user.getId(),
                userReceiverId);
    }

    private void createFriend(FriendRequest friendRequest) {
        Optional<FriendRequest> existingFriendRequest =
                friendRepository.findBySenderIdAndReceiverId(friendRequest.getReceiver().getId(),
                        friendRequest.getSender().getId());

        if (existingFriendRequest.isPresent()) {
            FriendRequest friend = existingFriendRequest.get();
            log.info(" Friend request from user id = '{}' for user id = '{}' has been updated to ACCEPTED status",
                    friendRequest.getReceiver().getId(),
                    friendRequest.getSender().getId());
            friend.setStatus(RequestStatus.ACCEPTED);
            friendRepository.save(friend);
        } else {
            FriendRequest newFriendRequest = new FriendRequest();
            newFriendRequest.setSender(friendRequest.getReceiver());
            newFriendRequest.setReceiver(friendRequest.getSender());
            newFriendRequest.setStatus(RequestStatus.ACCEPTED);
            friendRepository.save(newFriendRequest);
            log.info("created a new friendRequest from user id = '{}' for user id = '{}' with status ACCEPTED",
                    friendRequest.getReceiver().getId(),
                    friendRequest.getSender().getId());
        }
    }
    private void deleteFriend(FriendRequest friendRequest) {
        Optional<FriendRequest> existingFriendRequest =
                friendRepository.findBySenderIdAndReceiverId(friendRequest.getReceiver().getId(),
                        friendRequest.getSender().getId());

        if (existingFriendRequest.isPresent()) {
            log.info("Friend request status from user with id = '{}' to user with id = '{}' has been changed to DECLINED",
                    friendRequest.getReceiver().getId(),
                    friendRequest.getSender().getId());
            FriendRequest friend = existingFriendRequest.get();
            friend.setStatus(RequestStatus.DECLINED);
            friendRepository.save(friend);
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User with name={} not found", email);
            throw new ResourceNotFoundException("User not found" + email);
        });

    }
}




