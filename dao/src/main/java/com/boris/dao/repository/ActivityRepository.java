package com.boris.dao.repository;

import com.boris.dao.entity.Activity;
import com.boris.dao.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Optional<Activity> findByPostId(Long postId);
    Page<Activity> findAllByUserIdInOrderByCreatedAtDesc(List<Long> userIds, Pageable pageable);
    Page<Activity> findAllByUser_IdInOrderByCreatedAtDesc(List<Long> userIds, Pageable pageable);
}
