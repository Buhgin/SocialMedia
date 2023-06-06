package com.boris.business.service;

import com.boris.business.exception.ResourceNotFoundException;
import com.boris.business.model.enums.sort.PostSortBy;
import com.boris.business.model.enums.sort.SortType;
import com.boris.dao.entity.FriendRequest;
import com.boris.dao.repository.FriendRepository;
import com.boris.business.mapper.dto.PostMapper;
import com.boris.business.mapper.request.PostCreateMapper;
import com.boris.business.model.dto.PostDto;
import com.boris.business.model.dto.UserDto;
import com.boris.business.model.request.PostCreateRequest;
import com.boris.dao.entity.Post;
import com.boris.dao.entity.User;
import com.boris.dao.repository.PostRepository;
import com.boris.dao.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {
    private static final String userName = "testUser";
    private static final int pageNo = 0;
    private static final int pageSize = 10;
    private static final SortType sortType = SortType.DESC;
    private static final PostSortBy postSort = PostSortBy.CREATED_AT;
    private static final Sort sort = Sort.by(sortType.getDirection(), postSort.getAttribute());
    private static final Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

    private PostCreateRequest postCreateRequest;
    private User user;
    private Post post, post2;
    private UserDto userDto;
    private PostDto postDto,postDto2;


    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCreateMapper postCreateMapper;

    @Mock
    private PostMapper postMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private PostService postService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postCreateRequest = new PostCreateRequest("test title", "test description",
                "test image url", "test content");
        user = mock(User.class);
        post = mock(Post.class);
        post2 = mock(Post.class);
        post.setUser(user);
        post.setId(1L);
        post.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        userDto = mock(UserDto.class);// new UserDto("testUser", "test@ya.ru");
        postDto =mock(PostDto.class); //new PostDto(1L, "test description", "test image url", "test content", userDto, post.getCreatedAt());
        postDto2 = mock(PostDto.class);
        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

    }

    @Test
    void testCreate() {
        Post post = new Post();
        when(postCreateMapper.toEntity(postCreateRequest)).thenReturn(post);
        when(postRepository.save(any(Post.class))).then(returnsFirstArg());

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postMapper.toDto(postCaptor.capture())).thenReturn(postDto);

        PostDto result = postService.create(postCreateRequest, userName);

        verify(userRepository).findByEmail(userName);
        verify(postCreateMapper).toEntity(postCreateRequest);
        verify(postMapper).toDto(postCaptor.getValue());
        verify(postRepository).save(post);

        assertNotNull(result);
        assertEquals(postDto, result);
        assertEquals(user, post.getUser());
        assertNotNull(post.getCreatedAt());
    }

    @Test
    public void update() {
        when(post.getUser()).thenReturn(user);
        Post newPost = new Post();
        newPost.setUser(user);
        newPost.setId(post.getId());
        newPost.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        PostDto expectedPostDto = new PostDto(post.getId(), "test description", "test image url", "test content", userDto, newPost.getCreatedAt());

        when(postRepository.existsByIdAndUserId(post.getId(), user.getId())).thenReturn(true);
        when(postCreateMapper.toEntity(postCreateRequest)).thenReturn(newPost);
        when(postRepository.save(any(Post.class))).then(returnsFirstArg());
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedPostDto);

        PostDto actualPostDto = postService.update(post.getId(), postCreateRequest, userName);

        assertNotNull(actualPostDto);
        assertEquals(expectedPostDto, actualPostDto);
        assertEquals(user, newPost.getUser());
        verify(postRepository).existsByIdAndUserId(post.getId(), user.getId());
        verify(userRepository, times(2)).findByEmail(userName);
        verify(postRepository).findById(post.getId());
        verify(postCreateMapper).toEntity(postCreateRequest);
        verify(postMapper).toDto(newPost);
        verify(postRepository).save(newPost);
    }

    @Test
    public void update_shouldThrowExceptionAndNotSavePost_whenPostDoesNotBelongToUser() {
        when(postRepository.existsByIdAndUserId(post.getId(), user.getId())).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                postService.update(post.getId(), postCreateRequest, userName));

        String expectedMessage = "This post does not apply to the user " + userName;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        verify(postRepository, times(1)).existsByIdAndUserId(post.getId(), user.getId());
        verify(userRepository, times(1)).findByEmail(userName);
        verify(postRepository, times(0)).save(any(Post.class));
        verify(postMapper, times(0)).toDto(any(Post.class));
    }

    @Test
    public void deleteById() {

        when(user.getId()).thenReturn(2L);
        when(postRepository.existsByIdAndUserId(post.getId(), user.getId())).thenReturn(true);

        postService.deleteById(post.getId(), userName);

        verify(postRepository, times(1)).delete(post);
        verify(postRepository, times(1)).existsByIdAndUserId(post.getId(), user.getId());
        verify(userRepository, times(1)).findByEmail(userName);

    }

    @Test
    public void deleteByIdShouldThrowResourceNotFoundExceptionWhenPostDoesNotBelongsToUser() {

        User user = mock(User.class);
        when(user.getId()).thenReturn(2L);

        when(postRepository.existsByIdAndUserId(post.getId(), user.getId())).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                postService.deleteById(post.getId(), userName));

        String expectedMessage = "This post does not apply to the user " + userName;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteById_shouldThrowResourceNotFoundException_whenUserIsNotFound() {
        String userName = "testUser";

        when(userRepository.findByEmail(userName)).thenThrow(new ResourceNotFoundException("User not found with email " + userName));

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                postService.deleteById(post.getId(), userName));

        String expectedMessage = "User not found with email " + userName;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getByUserId_shouldThrowResourceNotFoundException_whenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                postService.getByUserId(userId, pageNo, pageSize, sortType, postSort));

        String expectedMessage = "User not found id " + userId;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(0)).findByUserId(anyLong(), any(Pageable.class));
        verify(postMapper, times(0)).toDtoSet(anySet());
    }

    @Test
    public void getByUserId() {
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Set<Post> postSet = new HashSet<>();
        postSet.add(post);
        Page<Post> postPage = new PageImpl<>(new ArrayList<>(postSet), pageable, postSet.size());
        when(postRepository.findByUserId(1L, pageable)).thenReturn(postPage);
        PostDto postDto = mock(PostDto.class);
        Set<PostDto> expectedPostDtoSet = new HashSet<>();
        expectedPostDtoSet.add(postDto);
        when(postMapper.toDtoSet(postSet)).thenReturn(expectedPostDtoSet);

        Set<PostDto> actualPostDtoSet = postService.getByUserId(1L, pageNo, pageSize, sortType, postSort);

        assertEquals(expectedPostDtoSet, actualPostDtoSet);
        verify(postRepository, times(1)).findByUserId(1L, pageable);
        verify(postMapper, times(1)).toDtoSet(postSet);
    }

    @Test
    public void getOne() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDto);
        assertEquals(postDto, postService.getOne(1L));
        verify(postRepository, times(1)).findById(1L);
        verify(postMapper, times(1)).toDto(post);

    }

    @Test
    public void getAllUsersSubscriptionActivities() {
        List<FriendRequest> friendList = new ArrayList<>();
        when(user.getId()).thenReturn(1L);
        when(friendRepository.findAllReceiverBySenderId(1L)).thenReturn(friendList);


        List<Post> postList = new ArrayList<>();
        postList.add(post);
        Page<Post> postPage = new PageImpl<>(new ArrayList<>(postList), pageable, postList.size());
        when(postRepository.findAllByUserIdInOrderByCreatedAtDesc(any(), any())).thenReturn(postPage);
        PostDto postDto = mock(PostDto.class);
        List<PostDto> expectedPostDto = new ArrayList<>();
        expectedPostDto.add(postDto);
        when(postMapper.toDtoList(postList)).thenReturn(expectedPostDto);

        List<PostDto> actualPostDtoSet = postService.getAllUsersSubscriptionActivities(userName, pageNo, pageSize, sortType, postSort);

        assertEquals(expectedPostDto, actualPostDtoSet);
        verify(postRepository, times(1)).findAllByUserIdInOrderByCreatedAtDesc(any(), any());
        verify(postMapper, times(1)).toDtoList(postList);
    }

    @Test
    public void getAllUsersSubscriptionActivities_shouldReturnEmptyList_whenUserHasNoSubscriptions() {

        when(friendRepository.findAllReceiverBySenderId(user.getId())).thenReturn(Collections.emptyList());

        when(postRepository.findAllByUserIdInOrderByCreatedAtDesc(any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));
        when(postMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<PostDto> actualPosts = postService.getAllUsersSubscriptionActivities(userName, pageNo, pageSize, sortType, postSort);

        assertTrue(actualPosts.isEmpty(), "The list of posts should be empty when the user has no subscriptions");
        verify(postRepository, times(1)).findAllByUserIdInOrderByCreatedAtDesc(any(), any());
        verify(postMapper, times(1)).toDtoList(Collections.emptyList());
    }

    @Test
    public void getByUserId_withMultiplePosts() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Set<Post> postSet = new HashSet<>();
        postSet.add(post);
        postSet.add(post2);

        Page<Post> postPage = new PageImpl<>(new ArrayList<>(postSet), pageable, postSet.size());

        Set<PostDto> expectedPostDtoSet = new HashSet<>();
        expectedPostDtoSet.add(postDto);
        expectedPostDtoSet.add(postDto2);
        when(postRepository.findByUserId(eq(user.getId()), any(Pageable.class))).thenReturn(postPage);
        when(postMapper.toDtoSet(postSet)).thenReturn(expectedPostDtoSet);

        Set<PostDto> actualPostDtoSet = postService.getByUserId(user.getId(), pageNo, pageSize, sortType, postSort);

        assertEquals(expectedPostDtoSet, actualPostDtoSet);
        verify(postRepository, times(1)).findByUserId(user.getId(), pageable);
        verify(postMapper, times(1)).toDtoSet(postSet);
    }
}