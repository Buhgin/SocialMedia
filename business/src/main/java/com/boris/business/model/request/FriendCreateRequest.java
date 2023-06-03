package com.boris.business.model.request;

public record FriendCreateRequest(
        Long userReceiverId,
        boolean accept
) {
}
