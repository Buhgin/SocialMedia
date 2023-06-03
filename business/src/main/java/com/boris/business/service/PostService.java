package com.boris.business.service;

import com.boris.business.exception.ResourceNotFoundException;
import com.boris.business.mapper.dto.PostMapper;
import com.boris.business.mapper.request.PostCreateMapper;
import com.boris.business.model.dto.PostDto;
import com.boris.business.model.enums.sort.PostSortBy;
import com.boris.business.model.enums.sort.SortType;
import com.boris.business.model.request.PostCreateRequest;
import com.boris.dao.entity.Activity;
import com.boris.dao.entity.Post;
import com.boris.dao.entity.User;
import com.boris.dao.repository.ActivityRepository;
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
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostCreateMapper postCreateMapper;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public PostDto create(PostCreateRequest postCreateRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found id "+ userId));
        Post post = postCreateMapper.toEntity(postCreateRequest);
        post.setUser(user);
        saveActivityPost(post,user,Activity.ActivityType.POST_CREATED);
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostDto update(Long id, PostCreateRequest postCreateRequest,String name) {
        if(authenticationUserCheckById(id,name)){
        Post post = postRepository.findById(id).orElseThrow(() ->{
            log.error("Post not found id " + id);
                    throw new ResourceNotFoundException("Post not found id " + id);}
        );
        User user = userRepository.findByEmail(name).orElseThrow(() ->{
                    log.error("User not found name " + name);
                    throw new ResourceNotFoundException("User not found name " + name);}
        );
        Post newPost = postCreateMapper.toEntity(postCreateRequest);
        newPost.setId(post.getId());
        newPost.setUser(post.getUser());
        Post updatedPost = postRepository.save(newPost);
        saveActivityPost(updatedPost,user,Activity.ActivityType.POST_UPDATED);
        return postMapper.toDto(updatedPost);}
        else{
            log.error("This post does not apply to the user "+ name);
            throw new ResourceNotFoundException("This post does not apply to the user "+ name);
    }}
    public void deleteById(Long id, String name) {
        if (authenticationUserCheckById(id, name)) {
            activityRepository.delete(activityRepository.findByPostId(id).orElseThrow(null));
            Post post = postRepository.findById(id).orElseThrow(() ->{
                        log.error("Post not found id " + id);
                        throw new ResourceNotFoundException("Post not found id " + id);
            });
            postRepository.delete(post);
        } else {
            log.error("This post does not apply to the user " + name);
            throw new ResourceNotFoundException("This post does not apply to the user " + name);
        }
    }
    public Set<PostDto> getByUserId(Long userid,
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
        Set<Post> postSet = posts.toSet();
        return postMapper.toDtoSet(postSet);
    }
    public PostDto getOne(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->{
        log.error("Post not found id "+id);
            throw new ResourceNotFoundException("Post not found id "+id);
        });
        return postMapper.toDto(post);
    }
    private boolean authenticationUserCheckById(Long postId,String name) {
        User user = userRepository.findByUsernameOrEmail(name,name).orElseThrow(() ->{
                    log.error("User not found name "+ name);
                    throw new ResourceNotFoundException("User not found name "+ name);
        });
        Post post = postRepository.findById(postId).orElseThrow(() ->{
                    log.error("Post not found id "+ postId);
                    throw new ResourceNotFoundException("Post not found id "+ postId);
        });
        return post.getUser().getId().equals(user.getId());
    }
    private void saveActivityPost(Post post, User user, Activity.ActivityType type) {
        Activity activity = new Activity();
        activity.setUser(user);
        activity.setPost(post);
        activity.setCreatedAt(LocalDateTime.now());
        activity.setType(type);
        activityRepository.save(activity);
    }
}
