package com.boris.controller;

import com.boris.business.model.dto.MessageDto;
import com.boris.business.model.enums.sort.MessageSortBy;
import com.boris.business.model.enums.sort.SortType;
import com.boris.business.model.request.MessageCreateRequest;
import com.boris.business.service.MessageService;
import com.boris.model.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.boris.util.UserSession.getCurrentUserName;

@RestController
@RequiredArgsConstructor
@RequestMapping("/${api.version}/messages")
@Tag(name = "Message", description = "Message related resource")
public class MessageController {
    private final MessageService messageService;
    @PostMapping
    @Operation(summary = "Create message", description = "Creating message and unique identifier assigning. Follows model's " +
            "constraints to avoid unhandled errors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message created and will be returned with id", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))}),
            @ApiResponse(responseCode = "409", description = "Message is already exists", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })
    })

    public MessageDto create(MessageCreateRequest messageCreateRequest) {
        return messageService.createMessage(messageCreateRequest, getCurrentUserName());
    }

    @GetMapping("/requests/chat/{receiverFriendId}")
    @Operation(summary = "Get chat messages", description = "Get chat messages with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages returned", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class)),
            }),
            @ApiResponse(responseCode = "400", description = "Invalid page or quantity", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })
    })
    public List<MessageDto> getChatMessages(Long receiverFriendId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                            @RequestParam(defaultValue = "10") @Positive Integer quantity,
                                            @RequestParam(defaultValue = "createdAt") MessageSortBy messageSortBy,
                                            @RequestParam(defaultValue = "ASC") SortType sortType) {
        return messageService.getChatMessage(getCurrentUserName(), receiverFriendId, page, quantity, sortType, messageSortBy);
    }

}
