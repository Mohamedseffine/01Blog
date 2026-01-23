package com.zone01oujda.moblogging.user.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.ConflictException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.user.dto.UpdateUserDto;
import com.zone01oujda.moblogging.user.dto.UserDto;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.FileUploadUtil;
import com.zone01oujda.moblogging.util.SecurityUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FileUploadUtil fileUploadUtil;
    private final String uploadDir;

    public UserService(UserRepository userRepository, FileUploadUtil fileUploadUtil,
            @Value("${files.uploadDirectory}") String uploadDir) {
        this.userRepository = userRepository;
        this.fileUploadUtil = fileUploadUtil;
        this.uploadDir = uploadDir;
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

        if (dto.getUsername() != null && !dto.getUsername().isBlank()
                && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new ConflictException("Username already in use");
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }

        MultipartFile profilePicture = dto.getProfilePicture();
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String uploadedPath = fileUploadUtil.upload(profilePicture);
            user.setProfilePictureUrl(uploadedPath);
        }

        User saved = userRepository.save(user);
        return toDto(saved);
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
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getProfilePictureUrl(),
                user.getFollowerCount(),
                user.getFollowingCount(),
                user.getCreatedAt()
        );
    }
}
