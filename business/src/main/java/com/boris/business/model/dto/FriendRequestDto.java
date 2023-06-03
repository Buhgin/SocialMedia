package com.boris.business.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public record FriendRequestDto(@Schema(example = "User sender name: Ivan, email: ivan@email.ru ")
                                 UserDto sender,
                               @Schema(example = "User receiver name: Boris, email: boris@email.ru ")
                               UserDto receiver) {
}
