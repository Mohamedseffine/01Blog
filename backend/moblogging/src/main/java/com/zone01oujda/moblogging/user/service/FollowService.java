package com.zone01oujda.moblogging.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.entity.Follow;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.BadRequestException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.notification.enums.NotificationType;
import com.zone01oujda.moblogging.notification.service.NotificationService;
import com.zone01oujda.moblogging.user.repository.FollowRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.SecurityUtil;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FollowService(FollowRepository followRepository, UserRepository userRepository,
            NotificationService notificationService) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public void followUser(Long targetUserId) {
        User follower = getCurrentUser();
        if (follower.getId().equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself");
        }
        User target = userRepository.findById(targetUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (followRepository.existsByFollowerIdAndFollowingId(follower.getId(), targetUserId)) {
            throw new BadRequestException("Already following this user");
        }

        followRepository.save(new Follow(follower, target));
        notificationService.createNotification(
            target,
            NotificationType.FOLLOW,
            follower.getUsername() + " started following you"
        );
    }

    public void unfollowUser(Long targetUserId) {
        User follower = getCurrentUser();
        followRepository.deleteByFollowerIdAndFollowingId(follower.getId(), targetUserId);
    }

    public List<Follow> getFollowers(Long userId) {
        return followRepository.findByFollowingId(userId);
    }

    public List<Follow> getFollowing(Long userId) {
        return followRepository.findByFollowerId(userId);
    }

    private User getCurrentUser() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return userRepository.findByUsernameOrEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
