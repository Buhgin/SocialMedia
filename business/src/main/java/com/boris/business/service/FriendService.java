package com.boris.business.service;

import com.boris.business.mapper.dto.FriendRequestMapper;
import com.boris.business.model.dto.FriendRequestDto;
import com.boris.business.model.request.FriendCreateRequest;
import com.boris.dao.entity.FriendRequest;
import com.boris.dao.entity.User;
import com.boris.dao.repository.FriendRepository;
import com.boris.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendRequestMapper friendRequestMapper;

    public void createFriendRequest(String nameSender,
                                    Long userReceiverId) {
        User userSender = userRepository.findByEmail(nameSender).orElseThrow(() -> {
            log.error("User with name={} not found", nameSender);
            throw new RuntimeException("User not found" + nameSender);
        });
        if (!friendRepository.existsBySenderIdAndReceiverId(userSender.getId(), userReceiverId)) {
            User userReceiver = userRepository.findById(userReceiverId).orElseThrow(() -> {
                log.error("User with id={} not found", userReceiverId);
                throw new RuntimeException("User not found" + userReceiverId);
            });
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setSender(userSender);
            friendRequest.setReceiver(userReceiver);
            friendRequest.setStatus(FriendRequest.RequestStatus.PENDING);
            friendRepository.save(friendRequest);
            log.info("Creating new friend");
        }
        log.info("Friend already exists");
    }

    public Set<FriendRequestDto> getFriendshipNotification(String userReceiver) {
        User user = userRepository.findByEmail(userReceiver).orElseThrow(() -> {
            log.error("User with name={} not found", userReceiver);
            throw new RuntimeException("User not found" + userReceiver);
        });
        List<FriendRequest> allByReceiverId = friendRepository.findAllByReceiverIdAndStatus(
                user.getId(), FriendRequest.RequestStatus.PENDING);
        if (allByReceiverId.size() != 0) {
            return allByReceiverId.stream()
                    .map(friendRequestMapper::toDto)
                    .collect(Collectors.toSet());
        }
        log.info("No friend requests");
        return new HashSet<>();
    }

    public void acceptFriendAllRequests(String receiverName) {
        User user = userRepository.findByEmail(receiverName).orElseThrow(() -> {
                    log.error("user not found");
                    return new RuntimeException("user not found");
                }
        );
        List<FriendRequest> friendRequests = friendRepository.findAllByReceiverIdAndStatus(
                user.getId(), FriendRequest.RequestStatus.PENDING);
        if (friendRequests.size() != 0) {

            friendRequests
                    .forEach(friendRequest -> {
                        friendRequest.setStatus(FriendRequest.RequestStatus.ACCEPTED);
                        friendRepository.save(friendRequest);
                        log.info("Friend request accepted from" + friendRequest.getReceiver().getUsername());
                    });
        }
        log.info("No friend requests");
    }

    public void acceptFriendRequest(FriendCreateRequest friendCreateRequest, String senderName) {
        User user = userRepository.findByEmail(senderName).orElseThrow(() -> {
                    log.error("user not found");
                    return new RuntimeException("user not found");
                }
        );
        FriendRequest friendRequest = friendRepository.findBySenderIdAndReceiverId(
                         friendCreateRequest.userReceiverId(),user.getId())
                .orElseThrow(() -> {
                    log.info("No friend requests");
                    return new RuntimeException("No friend requests");
                });
        if (friendCreateRequest.accept()) {
            friendRequest.setStatus(FriendRequest.RequestStatus.ACCEPTED);
            createFriend(friendRequest);
            log.info("Friend request accepted from" + friendRequest.getReceiver().getUsername());
        } else {
            friendRequest.setStatus(FriendRequest.RequestStatus.DECLINED);
            log.info("Friend request declined from" + friendRequest.getReceiver().getUsername());
        }
        friendRepository.save(friendRequest);
    }
    public void deleteFriendRequest(FriendCreateRequest friendCreateRequest, String senderName) {
        User user = userRepository.findByEmail(senderName).orElseThrow(() -> {
                    log.error("user not found");
                    return new RuntimeException("user not found");
                }
        );
        FriendRequest friendRequest = friendRepository.findBySenderIdAndReceiverId(
                user.getId(),friendCreateRequest.userReceiverId())
                .orElseThrow(() -> {
                    log.info("No friend requests");
                    return new RuntimeException("No friend requests");
                });
        friendRepository.delete(friendRequest);
        log.info("Friend request deleted from" + friendRequest.getReceiver().getUsername());
    }

    private void createFriend(FriendRequest friendRequest) {
        if (friendRepository.existsBySenderIdAndReceiverId(friendRequest.getReceiver().getId(),
                friendRequest.getSender().getId())) {
            log.info("Friend already exists status accepted");
            friendRepository.findBySenderIdAndReceiverId(friendRequest.getReceiver().getId(),
                    friendRequest.getSender().getId()).ifPresent(friend -> {
                friend.setStatus(FriendRequest.RequestStatus.ACCEPTED);
                friendRepository.save(friend);
            });
            return;
        }
        FriendRequest companion = new FriendRequest();
        companion.setSender(friendRequest.getReceiver());
        companion.setReceiver(friendRequest.getSender());
        companion.setStatus(FriendRequest.RequestStatus.ACCEPTED);
        friendRepository.save(companion);
    }

}




