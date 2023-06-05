package com.boris.business.service;
import com.boris.business.exception.ResourceNotFoundException;
import com.boris.business.model.enums.sort.PostSortBy;
import com.boris.business.model.enums.sort.SortType;
import com.boris.dao.entity.FriendRequest;
import com.boris.dao.repository.FriendRepository;
import org.junit.jupiter.api.Assertions;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;




public class PostServiceTest {

    private PostCreateRequest postCreateRequest;
    private User user;
    private Post post;
    private UserDto userDto;
    private PostDto postDto;
    private String userName;
    private int pageNo;
    private int pageSize;
    private SortType sortType;
    private PostSortBy postSort;

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
        pageNo = 0;
        pageSize = 10;
        sortType = SortType.DESC;
        postSort = PostSortBy.CREATED_AT;
        userName = "testUser";
        postCreateRequest = new PostCreateRequest("test title", "test description",
                "test image url", "test content");
        user = mock(User.class);
        post = mock(Post.class);
        post.setUser(user);
        post.setId(1L);
        post.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        userDto = new UserDto("testUser", "test@ya.ru");
        postDto = new PostDto(1L, "test description",
                "test image url", "test content", userDto, post.getCreatedAt());
    }

    @Test
    void testCreate() {
        Post post = new Post();

        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        when(postCreateMapper.toEntity(postCreateRequest)).thenReturn(post);
        when(postRepository.save(any(Post.class))).then(returnsFirstArg());
        when(postMapper.toDto(any(Post.class))).thenAnswer(invocation -> {
            Post postArg = invocation.getArgument(0);
            if (postArg == post) {
                return postDto;
            } else {
                return null;
            }
        });

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        PostDto result = postService.create(postCreateRequest, userName);

        verify(userRepository, times(1)).findByEmail(userName);
        verify(postCreateMapper, times(1)).toEntity(postCreateRequest);
        verify(postMapper, times(1)).toDto(post);
        verify(postRepository, times(1)).save(post);

        assertNotNull(result);
        assertEquals(postDto, result);
        assertEquals(user, post.getUser());
        assertEquals(now, post.getCreatedAt());
    }

    @Test
    public void update() {
        when(post.getUser()).thenReturn(user);

        Post newPost = new Post();
        newPost.setUser(user);
        newPost.setId(post.getId());
        newPost.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        UserDto userDto = new UserDto("testUser", "test@ya.ru");
        PostDto expectedPostDto = new PostDto(post.getId(), "test description", "test image url", "test content", userDto, newPost.getCreatedAt());

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        when(postRepository.existsByIdAndUserId(post.getId(), user.getId())).thenReturn(true);
        when(postCreateMapper.toEntity(postCreateRequest)).thenReturn(newPost);
        when(postRepository.save(any(Post.class))).then(returnsFirstArg());
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedPostDto);

        PostDto actualPostDto = postService.update(post.getId(), postCreateRequest, userName);

        assertNotNull(actualPostDto);
        assertEquals(expectedPostDto, actualPostDto);
        assertEquals(user, newPost.getUser());
        verify(postRepository, times(1)).existsByIdAndUserId(post.getId(), user.getId());
        verify(userRepository, times(2)).findByEmail(userName);
        verify(postRepository, times(1)).findById(post.getId());
        verify(postCreateMapper, times(1)).toEntity(postCreateRequest);
        verify(postMapper, times(1)).toDto(newPost);
        verify(postRepository, times(1)).save(newPost);
    }

    @Test
    public void update_shouldThrowResourceNotFoundException_whenUserIsNotAuthenticated() {

        //User user = mock(User.class);
        when(user.getId()).thenReturn(2L);
        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        when(postRepository.existsByIdAndUserId(post.getId(), user.getId())).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                postService.update(post.getId(), postCreateRequest, userName));

        String expectedMessage = "This post does not apply to the user " + userName;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    public void update_shouldThrowResourceNotFoundException_whenPostNotFound() {
        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        when(postRepository.existsByIdAndUserId(post.getId(), user.getId())).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                postService.update(post.getId(), postCreateRequest, userName));

        String expectedMessage = "This post does not apply to the user " + userName;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    public void update_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findByEmail(userName)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                postService.update(post.getId(), postCreateRequest, userName));

        String expectedMessage = "This post does not apply to the user " + userName;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteById() {
        Long postId = 1L;
        String userName = "testUser";

        User user = mock(User.class);
        when(user.getId()).thenReturn(2L);  // assuming user ID is 2
        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        when(postRepository.existsByIdAndUserId(postId, user.getId())).thenReturn(true);

        Post post = mock(Post.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deleteById(postId, userName);

        verify(postRepository, times(1)).delete(post);
    }

    @Test
    public void deleteByIdShouldThrowResourceNotFoundExceptionWhenPostDoesNotBelongsToUser() {

        User user = mock(User.class);
        when(user.getId()).thenReturn(2L);  // assuming user ID is 2
        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        when(postRepository.existsByIdAndUserId(post.getId(), user.getId())).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                postService.deleteById(post.getId(), userName));

        String expectedMessage = "This post does not apply to the user " + userName;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getByUserId() {
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Sort sort = Sort.by(sortType.getDirection(), postSort.getAttribute());
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Set<Post> postSet = new HashSet<>();
        postSet.add(post);
        Page<Post> postPage = new PageImpl<>(new ArrayList<>(postSet), pageable, postSet.size());
        when(postRepository.findByUserId(1L, pageable)).thenReturn(postPage);
        PostDto postDto = mock(PostDto.class);
        Set<PostDto> expectedPostDtoSet = new HashSet<>();
        expectedPostDtoSet.add(postDto);
        when(postMapper.toDtoSet(postSet)).thenReturn(expectedPostDtoSet);

        Set<PostDto> actualPostDtoSet = postService.getByUserId(1L, pageNo, pageSize, sortType, postSort);

        // Assert
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
        when(userRepository.findByEmail(userName)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);
        when(friendRepository.findAllReceiverBySenderId(1L)).thenReturn(friendList);

        Sort sort = Sort.by(sortType.getDirection(), postSort.getAttribute());
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

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
}