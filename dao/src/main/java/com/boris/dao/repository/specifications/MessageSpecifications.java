package com.boris.dao.repository.specifications;

import com.boris.dao.entity.Message;
import org.springframework.data.jpa.domain.Specification;

public class MessageSpecifications {
    public static Specification<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId) {
        return (root, query, cb) -> cb.or(
                cb.and(
                        cb.equal(root.get("sender").get("id"), senderId),
                        cb.equal(root.get("receiver").get("id"), receiverId)
                ),
                cb.and(
                        cb.equal(root.get("sender").get("id"), receiverId),
                        cb.equal(root.get("receiver").get("id"), senderId)
                )
        );
    }
}
