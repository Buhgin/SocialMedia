package com.boris.dao.repository;

import com.boris.dao.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Long userId, Pageable pageable);
    Page<Post>findAllByUserIdInOrderByCreatedAtDesc(List<Long> userId,Pageable pageable);
    boolean existsByIdAndUserId(Long postId, Long userId);
}
