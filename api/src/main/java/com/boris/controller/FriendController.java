package com.boris.controller;

import com.boris.business.model.dto.FriendRequestDto;
import com.boris.business.model.dto.PostDto;
import com.boris.business.model.request.FriendCreateRequest;
import com.boris.business.service.FriendService;
import com.boris.model.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static com.boris.util.UserSession.getCurrentUserName;

@RestController
@RequiredArgsConstructor
@RequestMapping("/${api.version}/friends")
@Tag(name = "FriendRequest", description = "FriendRequest related resource")
public class FriendController {
    private final FriendService friendService;

    @PostMapping("users/{userReceiverId}")
    @Operation(summary = "Create friend request", description = "Creating friend request and unique identifier assigning. Follows model's " +
            "constraints to avoid unhandled errors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request created and will be returned with id", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FriendRequestDto.class))}),
            @ApiResponse(responseCode = "409", description = "User receiver id is not valid", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    public ResponseEntity<FriendRequestDto> create(@PathVariable(value = "userReceiverId")
                                                   Long userReceiverId) {
        FriendRequestDto friendRequestDto = friendService.createFriendRequest(getCurrentUserName(), userReceiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(friendRequestDto);
    }

    @GetMapping("/requests")
    @Operation(summary = "Get all friend requests", description = "Get all friend requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend requests returned", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Friend requests not found", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })
    })
    public Set<FriendRequestDto> getAllFriendRequests() {
        return friendService.getFriendshipNotifications(getCurrentUserName());
    }

    @GetMapping
    @Operation(summary = "Get all friends", description = "Get all friends")
    @ApiResponses(value = {
       @ApiResponse(responseCode = "200", description = "Friends returned", content = {
               @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FriendRequestDto.class))
       }),
            @ApiResponse(responseCode = "404", description = "Friends not found", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })
    })
    public Set<FriendRequestDto> getAllFriends() {
        return friendService.getAllFriends(getCurrentUserName());
    }

    @PutMapping("/accepts")
    @Operation(summary = "Accept all friend requests", description = "Accept all friend requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend requests accepted", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FriendRequestDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })
    })

    public Set<FriendRequestDto> acceptFriendRequest() {
        return friendService.acceptFriendAllRequests(getCurrentUserName());
    }

    @PutMapping("/accept")
    @Operation(summary = "Accept or deny one friend request")
    @ApiResponses(value = {
         @ApiResponse(responseCode = "200", description = "Friend request accepted", content = {
                 @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FriendRequestDto.class))
         }),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })
    })
    public FriendRequestDto acceptOneFriendRequest(@Valid @RequestBody FriendCreateRequest friendCreateRequest) {
        return friendService.acceptFriendRequest(friendCreateRequest, getCurrentUserName());
    }

    @DeleteMapping("/delete/{friendId}")
    @Operation(summary = "Delete friend request")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "204", description = "Friend request deleted", content = {
                   @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FriendRequestDto.class))
           }),

            @ApiResponse(responseCode = "409", description = "Not friendRequest found for user with ID= ", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })
    })
    public ResponseEntity<HttpStatus> deleteFriend(@PathVariable(value = "friendId") Long friendId) {
        friendService.deleteFriendRequest(friendId, getCurrentUserName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
