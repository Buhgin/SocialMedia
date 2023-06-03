package com.boris.controller;

import com.boris.business.model.dto.PostDto;
import com.boris.business.model.enums.sort.PostSortBy;
import com.boris.business.model.enums.sort.SortType;
import com.boris.business.model.request.PostCreateRequest;
import com.boris.business.service.PostService;
import com.boris.model.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/${api.version}/posts")
@Tag(name = "Post", description = "Post related resource")
public class PostController {
    private final PostService postService;

    @PostMapping("users/{userId}")
    @Operation(summary = "Create post", description = "Creating post and unique identifier assigning. Follows model's " +
            "constraints to avoid unhandled errors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created and will be returned with id", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "409", description = "Post is already exists", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
            })
    })
    public ResponseEntity<PostDto> create(@PathVariable(value = "userId") Long userId,
                                          @Valid @RequestBody PostCreateRequest postCreateRequest) {
        PostDto post = postService.create(postCreateRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @GetMapping(("users/{userId}/posts"))
    @Operation(summary = "Get all posts by User id", description = "Get all posts by user with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Posts returned", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Invalid page or quantity", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
    })
    public Set<PostDto> findPostUserID(@Valid @PathVariable(value = "userId") Long useId,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                       @RequestParam(defaultValue = "10") @Positive Integer quantity,
                                       @RequestParam(defaultValue = "ID") PostSortBy companySortBy,
                                       @RequestParam(defaultValue = "ASC") SortType sortType) {
        return postService.getByUserId(useId, page, quantity, sortType, companySortBy);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get post by id", description = "Get post by id")
    @ApiResponse(responseCode = "200", description = "Post returned", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Invalid id", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
    })
    public PostDto getOne(@PathVariable(value = "postId") Long id) {
        return postService.getOne(id);
    }

    @PutMapping("{postId}")
    @Operation(summary = "Update post", description = "Update post by id")
    @ApiResponse(responseCode = "200", description = "Post updated", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
    })
    public PostDto updatePost(@Valid @RequestBody PostCreateRequest postCreateRequest,
                              @PathVariable(name = "postId") Long id) {

        return postService.update(id, postCreateRequest, getCurrentUserName());
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete post", description = "Delete post by id")
    @ApiResponse(responseCode = "204", description = "Post deleted", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostDto.class))
    })
    @ApiResponse(responseCode = "400", description = "Invalid id", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))
    })
    public ResponseEntity<HttpStatus> delete(@PathVariable(value = "postId") Long id) {
        postService.deleteById(id, getCurrentUserName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }
}
