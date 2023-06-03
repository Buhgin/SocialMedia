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
                throw new RuntimeException("User not found" + userReceiverId);
            });
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setSender(userSender);
            friendRequest.setReceiver(userReceiver);
            friendRequest.setStatus(RequestStatus.PENDING);
            friendRepository.save(friendRequest);
            log.info("Creating new friend");
        }
        log.info("Friend already exists");
        return friendRequestMapper.toDto(friendRepository.findBySenderIdAndReceiverId(userSender.getId(), userReceiverId).orElseThrow(() -> {
            log.error("Friend not found");
            throw new RuntimeException("Friend not found");
        }));
    }

    public Set<FriendRequestDto> getFriendshipNotifications(String userReceiver) {
        List<FriendRequest> allByReceiverId = friendRepository.findAllByReceiverIdAndStatus(
                getUser(userReceiver).getId(), RequestStatus.PENDING);
        if (allByReceiverId.size() != 0) {
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
                        log.info("Friend request accepted from" + friendRequest.getReceiver().getUsername());
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
                    log.info("No friend request found for user with ID= {}", friendCreateRequest.userReceiverId());
                    return new RuntimeException("No friend requests");
                });
        if (friendCreateRequest.accept()) {
            friendRequest.setStatus(RequestStatus.ACCEPTED);
            createFriend(friendRequest);
            friendRepository.save(friendRequest);
            log.info("Friend request accepted from" + friendRequest.getReceiver().getUsername());
        } else {
            friendRequest.setStatus(RequestStatus.DECLINED);
            friendRepository.save(friendRequest);
            log.info("Friend request declined from" + friendRequest.getReceiver().getUsername());
        }

        return friendRequestMapper.toDto(friendRequest);
    }

    public void deleteFriendRequest(Long userReceiverId, String senderName) {
        User user = getUser(senderName);
        FriendRequest friendRequest = friendRepository.findBySenderIdAndReceiverId(
                        user.getId(), userReceiverId)
                .orElseThrow(() -> {
                    log.info("No friend requests");
                    return new RuntimeException("No friend requests");
                });
        deleteFriend(friendRequest);
        friendRepository.delete(friendRequest);
        log.info("Friend request deleted from" + friendRequest.getReceiver().getUsername());
    }

    private void createFriend(FriendRequest friendRequest) {
        Optional<FriendRequest> existingFriendRequest =
                friendRepository.findBySenderIdAndReceiverId(friendRequest.getReceiver().getId(),
                        friendRequest.getSender().getId());

        if (existingFriendRequest.isPresent()) {
            FriendRequest friend = existingFriendRequest.get();
            log.info("Friend already exists with status ACCEPTED");
            friend.setStatus(RequestStatus.ACCEPTED);
            friendRepository.save(friend);
        } else {
            FriendRequest newFriendRequest = new FriendRequest();
            newFriendRequest.setSender(friendRequest.getReceiver());
            newFriendRequest.setReceiver(friendRequest.getSender());
            newFriendRequest.setStatus(RequestStatus.ACCEPTED);
            friendRepository.save(newFriendRequest);
            log.info("New friend request created with status ACCEPTED");
        }
    }
    private void deleteFriend(FriendRequest friendRequest) {
        Optional<FriendRequest> existingFriendRequest =
                friendRepository.findBySenderIdAndReceiverId(friendRequest.getReceiver().getId(),
                        friendRequest.getSender().getId());

        if (existingFriendRequest.isPresent()) {
            log.info("The friend's status will be changed to DECLINED");
            FriendRequest friend = existingFriendRequest.get();
            friend.setStatus(RequestStatus.DECLINED);
            friendRepository.save(friend);
        }
    }

    private User getUser(String name) {
        return userRepository.findByEmail(name).orElseThrow(() -> {
            log.error("User with name={} not found", name);
            throw new RuntimeException("User not found" + name);
        });

    }
}




