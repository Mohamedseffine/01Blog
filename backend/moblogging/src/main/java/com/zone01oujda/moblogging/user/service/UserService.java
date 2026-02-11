package com.zone01oujda.moblogging.user.service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.ConflictException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.user.dto.UpdateUserDto;
import com.zone01oujda.moblogging.user.dto.UserDto;
import com.zone01oujda.moblogging.user.repository.FollowRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.FileUploadUtil;
import com.zone01oujda.moblogging.util.SecurityUtil;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final FileUploadUtil fileUploadUtil;
    private final String uploadDir;

    public UserService(UserRepository userRepository, FollowRepository followRepository, FileUploadUtil fileUploadUtil,
            @Value("${files.uploadDirectory}") String uploadDir) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.fileUploadUtil = fileUploadUtil;
        this.uploadDir = uploadDir;
    }

    public Page<UserDto> getAllUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        List<User> allUsers = userRepository.findAll();
        
        List<User> filtered = allUsers.stream()
                .filter(user -> {
                    if (search == null || search.trim().isEmpty()) {
                        return true;
                    }
                    String lowerSearch = search.toLowerCase().trim();
                    return user.getUsername().toLowerCase().contains(lowerSearch) ||
                           user.getEmail().toLowerCase().contains(lowerSearch) ||
                           (user.getBio() != null && user.getBio().toLowerCase().contains(lowerSearch));
                })
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<UserDto> pageContent = filtered.subList(start, end).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toDto(user);
    }

    public UserDto getCurrentUser() {
        User user = getCurrentUserEntity();
        return toDto(user);
    }

    public UserDto updateUser(Long userId, UpdateUserDto dto) {
        User currentUser = getCurrentUserEntity();
        boolean isAdmin = SecurityUtil.hasRole("ADMIN");
        if (!isAdmin && !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You cannot update this profile");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String trimmedUsername = trimToNull(dto.getUsername());
        if (trimmedUsername != null && !trimmedUsername.equals(user.getUsername())) {
            if (userRepository.existsByUsername(trimmedUsername)) {
                throw new ConflictException("Username already in use");
            }
            user.setUsername(trimmedUsername);
        }

        if (dto.getBio() != null) {
            user.setBio(trimToNull(dto.getBio()));
        }

        MultipartFile profilePicture = dto.getProfilePicture();
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String uploadedPath = fileUploadUtil.upload(profilePicture);
            user.setProfilePictureUrl(uploadedPath);
        }

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public UserDto updateCurrentUser(UpdateUserDto dto) {
        User currentUser = getCurrentUserEntity();
        return updateUser(currentUser.getId(), dto);
    }

    public Resource getProfilePicture(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String picturePath = user.getProfilePictureUrl();
        if (picturePath == null || picturePath.isBlank()) {
            throw new ResourceNotFoundException("Profile picture not found");
        }

        String relative = picturePath.startsWith("/") ? picturePath.substring(1) : picturePath;
        String normalizedBase = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";

        Path filePath;
        if (relative.startsWith(normalizedBase)) {
            filePath = Paths.get(relative);
        } else {
            filePath = Paths.get(uploadDir, relative);
        }

        Path normalizedBasePath = Paths.get(uploadDir).normalize();
        Path normalizedFile = filePath.normalize();
        if (!normalizedFile.startsWith(normalizedBasePath)) {
            throw new ResourceNotFoundException("Profile picture not found");
        }

        try {
            Resource resource = new UrlResource(normalizedFile.toUri());
            if (!resource.exists()) {
                throw new ResourceNotFoundException("Profile picture not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Profile picture not found");
        }
    }

    private User getCurrentUserEntity() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("Unauthenticated");
        }
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserDto toDto(User user) {
        return toDtoWithFollowStatus(user, getCurrentUserEntityOrNull());
    }

    private UserDto toDtoWithFollowStatus(User user, User currentUser) {
        boolean isFollowing = false;
        if (currentUser != null && !currentUser.getId().equals(user.getId())) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), user.getId());
        }
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getProfilePictureUrl(),
                user.getFollowerCount(),
                user.getFollowingCount(),
                user.getCreatedAt(),
                isFollowing
        );
    }

    private User getCurrentUserEntityOrNull() {
        try {
            return getCurrentUserEntity();
        } catch (Exception e) {
            return null;
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
