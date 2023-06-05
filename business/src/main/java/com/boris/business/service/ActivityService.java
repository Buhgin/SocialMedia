package com.boris.business.service;

import com.boris.business.mapper.dto.ActivityMapper;
import com.boris.business.mapper.dto.PostMapper;
import com.boris.business.model.dto.ActivityDto;
import com.boris.business.model.dto.PostDto;
import com.boris.business.model.enums.sort.ActivitySort;
import com.boris.business.model.enums.sort.PostSortBy;
import com.boris.business.model.enums.sort.SortType;
import com.boris.dao.entity.Activity;
import com.boris.dao.entity.FriendRequest;
import com.boris.dao.entity.Post;
import com.boris.dao.entity.User;
import com.boris.dao.repository.ActivityRepository;
import com.boris.dao.repository.FriendRepository;
import com.boris.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ActivityService {
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final FriendRepository friendRepository;
    private final PostMapper postMapper;

 /*   public Set<PostDto> getAllUsersSubscriptionActivities(String name, int pageNo,
                                                          int pageSize,
                                                          SortType sortType,
                                                          ActivitySort activitySort) {
        User user= userRepository.findByEmail(name).orElseThrow(() -> new RuntimeException("User not found"));
        List<FriendRequest> friends = friendRepository.findAllReceiverBySenderId(user.getId());
        List<Long> receiverIds = friends.stream()
                .map(friend -> friend.getReceiver().getId())
                .toList();
        Sort sort = Sort.by(sortType.getDirection(), activitySort.getAttribute());
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Activity> activities = activityRepository.findAllByUserIdInOrderByCreatedAtDesc(receiverIds, pageable);
        List<Activity> activitiesSet = activities.toList();
        log.info("Activities: {}", activitiesSet);
        return activitiesSet.stream()
                .map(Activity::getPost)
                .map(postMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));

    }*/

}
