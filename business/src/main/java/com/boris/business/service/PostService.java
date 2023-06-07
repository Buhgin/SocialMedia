package com.boris.business.service;

import com.boris.business.exception.ResourceNotFoundException;
import com.boris.business.mapper.dto.PostMapper;
import com.boris.business.mapper.request.PostCreateMapper;
import com.boris.business.model.dto.PostDto;
import com.boris.business.model.enums.sort.PostSortBy;
import com.boris.business.model.enums.sort.SortType;
import com.boris.business.model.request.PostCreateRequest;
import com.boris.business.model.response.PostResponse;
import com.boris.dao.entity.FriendRequest;
import com.boris.dao.entity.Post;
import com.boris.dao.entity.User;
import com.boris.dao.repository.FriendRepository;
import com.boris.dao.repository.PostRepository;
import com.boris.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostCreateMapper postCreateMapper;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public PostDto create(PostCreateRequest postCreateRequest, String userName) {
        User user = getUser(userName);
        log.info("user found name = '{}', id = '{}'",user.getUsername(),user.getId());
        Post post = postCreateMapper.toEntity(postCreateRequest);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        postRepository.save(post);
        log.info("post title = '{}', id = '{}' created",post.getTitle(),post.getId());
        return postMapper.toDto(post);
    }
    public PostDto update(Long postId, PostCreateRequest postCreateRequest,String name) {
         if (postRepository.existsByIdAndUserId(postId, getUser(name).getId())){
        Post post = getPost(postId);
            log.info("post title = '{}', id = '{}' found"+post.getTitle(),post.getId());
        User user = getUser(name);
            log.info("user found name = '{}', id = '{}'"+user.getUsername(),user.getId());
        Post newPost = postCreateMapper.toEntity(postCreateRequest);
        newPost.setId(post.getId());
        newPost.setUser(post.getUser());
        newPost.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        Post updatedPost = postRepository.save(newPost);
            log.info("post title = '{}', id = '{}' updated ",updatedPost.getTitle(),updatedPost.getId());
        return postMapper.toDto(updatedPost);}
        else{
            log.error("This post does not apply to the user id = '{}' ", getUser(name).getId());
            throw new ResourceNotFoundException("This post does not apply to the user ID = "+ getUser(name).getId());
    }}
    public void deleteById(Long postId, String name) {
        if (postRepository.existsByIdAndUserId(postId, getUser(name).getId())) {
            Post post = getPost(postId);
            postRepository.delete(post);
            log.info("post deleted postId = '{}' ",postId);
        } else {
            log.error("This post does not apply to the user id = '{}' ",getUser(name).getId());
            throw new ResourceNotFoundException("This post does not apply to the user ID = " +getUser(name).getId());
        }
    }
    public PostResponse getByUserId(Long userid,
                                    int pageNo,
                                    int pageSize,
                                    SortType sortType,
                                    PostSortBy postSort) {
        User user = userRepository.findById(userid).orElseThrow(() ->{
            log.error("User not found id "+ userid);
                throw new ResourceNotFoundException("User not found id "+ userid);
                });
        Sort sort = Sort.by(sortType.getDirection(), postSort.getAttribute());
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Post> posts = postRepository.findByUserId(user.getId(), pageable);
        List<Post> postList = posts.toList();
        log.info("posts of user id = '{}' requested",user.getId());
        List<PostDto> postDtoList = postMapper.toDtoList(postList);

        return PostResponse.builder()
                .content(postDtoList)
                .pageNo(posts.getNumber())
                .pageSize(posts.getSize())
                .totalPages(posts.getTotalPages())
                .totalElements(posts.getTotalElements())
                .isLast(posts.isLast())
                .build();
    }
    public PostDto getOne(Long id) {
        Post post = getPost(id);
        log.info("post id ='{}' found title = '{}'",post.getId(),post.getTitle());
        return postMapper.toDto(post);
    }
    public PostResponse getAllUsersSubscriptionActivities(String name,
                                                          int pageNo,
                                                          int pageSize,
                                                          SortType sortType,
                                                          PostSortBy postSort) {
        User user = getUser(name);
        List<FriendRequest> friends = friendRepository.findAllReceiverBySenderId(user.getId());
        List<Long> receiverIds = friends.stream()
                .map(friend -> friend.getReceiver().getId())
                .toList();
        Sort sort = Sort.by(sortType.getDirection(), postSort.getAttribute());
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Post> pagePost = postRepository.findAllByUserIdInOrderByCreatedAtDesc(receiverIds, pageable);
        List<Post> posts = pagePost.toList();
        log.info("user id ='{}' requested subscription posts",user.getId());
        List<PostDto> postDtoList = postMapper.toDtoList(posts);
        return PostResponse.builder()
                .content(postDtoList)
                .pageNo(pagePost.getNumber())
                .pageSize(pagePost.getSize())
                .totalPages(pagePost.getTotalPages())
                .totalElements(pagePost.getTotalElements())
                .isLast(pagePost.isLast())
                .build();
    }

    private User getUser(String userName){
        return  userRepository.findByEmail(userName).orElseThrow(() ->{
            log.error("User not found name = '{}' ", userName);
            throw new ResourceNotFoundException("User not found name "+userName);
        });
    }
    private Post getPost(Long postId){
        return postRepository.findById(postId).orElseThrow(() ->{
            log.error("Post not found id = '{}'",postId);
            throw new ResourceNotFoundException("Post not found id "+ postId);
        });
    }

}
