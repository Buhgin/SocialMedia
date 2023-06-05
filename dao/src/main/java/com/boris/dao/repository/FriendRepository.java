package com.boris.dao.repository;

import com.boris.dao.entity.FriendRequest;

import com.boris.dao.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<FriendRequest, Long> {

  List<FriendRequest> findAllReceiverBySenderId(Long senderId);
  Optional<FriendRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
  boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
  List<FriendRequest> findAllByReceiverIdAndStatus(Long userReceiverId, RequestStatus status);
  Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, RequestStatus status);
}
