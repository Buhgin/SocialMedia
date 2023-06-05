package com.boris.business.model.dto;
import com.boris.dao.enums.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;


public record FriendRequestDto(@Schema(example = "User sender name: Ivan, email: ivan@email.ru ")
                               UserDto sender,
                               @Schema(example = "User receiver name: Boris, email: boris@email.ru ")
                               UserDto receiver,
                               @Schema(example = "PENDING", description = "PENDING, ACCEPTED, DECLINED")
                               RequestStatus status ) {
}
