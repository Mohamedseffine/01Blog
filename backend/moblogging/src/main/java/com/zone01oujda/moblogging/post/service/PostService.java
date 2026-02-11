package com.zone01oujda.moblogging.post.service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zone01oujda.moblogging.comment.dto.CommentDto;
import com.zone01oujda.moblogging.comment.util.CommentMapper;
import com.zone01oujda.moblogging.entity.Follow;
import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.BadRequestException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.notification.enums.NotificationType;
import com.zone01oujda.moblogging.notification.service.NotificationService;
import com.zone01oujda.moblogging.post.dto.CreatePostDto;
import com.zone01oujda.moblogging.post.dto.PostDto;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.user.repository.FollowRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.FileUploadUtil;
import com.zone01oujda.moblogging.util.SecurityUtil;

/**
 * Service class for post operations
 */
@Service
public class PostService {

    private final FileUploadUtil fileUploadUtil;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;
    private final com.zone01oujda.moblogging.report.repository.ReportRepository reportRepository;
    private final String uploadDir;

    public PostService(PostRepository postRepository, UserRepository userRepository, FileUploadUtil fileUploadUtil,
            FollowRepository followRepository,
            NotificationService notificationService,
            com.zone01oujda.moblogging.report.repository.ReportRepository reportRepository,
            @Value("${files.uploadDirectory}") String uploadDir) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.fileUploadUtil = fileUploadUtil;
        this.followRepository = followRepository;
        this.notificationService = notificationService;
        this.reportRepository = reportRepository;
        this.uploadDir = uploadDir;
    }

    /**
     * Create a new post with media files
     * @param dto the post creation data
     * @return PostDto with created post details
     * @throws AccessDeniedException if user is not authenticated
     * @throws BadRequestException if post data is invalid
     * @throws ResourceNotFoundException if user not found
     */
    public PostDto createPost(CreatePostDto dto) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        // Validate post data
        validatePostData(dto);

        // Get authenticated user
        User user = userRepository.findByUsernameOrEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Upload media files and collect URLs
        String mediaUrls = uploadMediaFiles(dto.multipartFiles);

        // Create and save post
        String title = trimToNull(dto.postTitle);
        String content = trimToNull(dto.postContent);
        String[] subjects = normalizeSubjects(dto.postSubject);
        if (title == null || content == null || subjects.length == 0) {
            throw new BadRequestException("Post data is invalid");
        }
        Post post = new Post(
            String.join(",", subjects),
            content,
            mediaUrls,
            dto.postVisibility,
            title
        );
        post.setCreator(user);
        post.setMediaUrl(mediaUrls);
        post = postRepository.save(post);

        List<Follow> followers = followRepository.findByFollowingId(user.getId());
        for (Follow follow : followers) {
            User receiver = follow.getFollower();
            if (receiver != null && !receiver.getId().equals(user.getId())) {
                notificationService.createNotification(
                    receiver,
                    NotificationType.POST,
                    user.getUsername() + " created a new post: " + post.getTitle()
                );
            }
        }

        // Convert to DTO and return
        return convertToDto(post);
    }

    /**
     * Get post by ID with comments
     * @param postId the post ID
     * @return PostDto with post details and comments
     * @throws ResourceNotFoundException if post not found
     */
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        boolean isAdmin = SecurityUtil.hasRole("ADMIN");
        if (Boolean.TRUE.equals(post.getHidden()) && !isAdmin) {
            throw new ResourceNotFoundException("Post not found");
        }

        return convertToDto(post);
    }

    public Page<PostDto> getPosts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (SecurityUtil.hasRole("ADMIN")) {
            return postRepository.findAll(pageable).map(this::convertToDto);
        }
        User currentUser = requireAuthenticatedUser();

        List<Long> creatorIds = new ArrayList<>();
        creatorIds.add(currentUser.getId());
        for (Follow follow : followRepository.findByFollowerId(currentUser.getId())) {
            if (follow.getFollowing() != null && follow.getFollowing().getId() != null) {
                creatorIds.add(follow.getFollowing().getId());
            }
        }
        // Preserve order but remove duplicates
        Set<Long> distinctCreatorIds = new LinkedHashSet<>(creatorIds);
        if (distinctCreatorIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return postRepository.findByCreatorIdInAndHiddenFalse(distinctCreatorIds, pageable)
                .map(this::convertToDto);
    }

    public Page<PostDto> getUserPosts(Long userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (SecurityUtil.hasRole("ADMIN")) {
            return postRepository.findForAdmin(null, null, userId, pageable).map(this::convertToDto);
        }

        return postRepository.findByCreatorIdAndHiddenFalse(userId, pageable)
                .map(this::convertToDto);
    }

    public PostDto updatePost(Long postId, com.zone01oujda.moblogging.post.dto.UpdatePostDto dto) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        boolean isAdmin = SecurityUtil.hasRole("ADMIN");
        if (!isAdmin && (post.getCreator() == null || !username.equals(post.getCreator().getUsername()))) {
            throw new AccessDeniedException("You cannot update this post");
        }

        String newTitle = trimToNull(dto.getPostTitle());
        if (newTitle != null) {
            post.setTitle(newTitle);
        }
        String newContent = trimToNull(dto.getPostContent());
        if (newContent != null) {
            post.setContent(newContent);
        }
        if (dto.getPostSubject() != null && dto.getPostSubject().length > 0) {
            String[] subjects = normalizeSubjects(dto.getPostSubject());
            if (subjects.length > 0) {
                post.setSubject(String.join(",", subjects));
            }
        }
        if (dto.getPostVisibility() != null) {
            post.setPostVisibility(dto.getPostVisibility());
        }

        if (dto.getMultipartFiles() != null && dto.getMultipartFiles().length > 0) {
            deleteExistingMedia(post.getMediaUrl());
            String mediaUrls = uploadMediaFiles(dto.getMultipartFiles());
            post.setMediaUrl(mediaUrls);
        }

        Post saved = postRepository.save(post);
        return convertToDto(saved);
    }

    @Transactional
    public void deletePost(Long postId) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        boolean isAdmin = SecurityUtil.hasRole("ADMIN");
        if (!isAdmin && (post.getCreator() == null || !username.equals(post.getCreator().getUsername()))) {
            throw new AccessDeniedException("You cannot delete this post");
        }

        reportRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }

    public Resource getPostMedia(Long postId, int index) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (post.getMediaUrl() == null || post.getMediaUrl().isBlank()) {
            throw new ResourceNotFoundException("Media not found");
        }

        String[] paths = post.getMediaUrl().split(",");
        if (index < 0 || index >= paths.length) {
            throw new ResourceNotFoundException("Media not found");
        }

        String storedPath = paths[index].trim();
        if (storedPath.isEmpty()) {
            throw new ResourceNotFoundException("Media not found");
        }

        String relative = storedPath.startsWith("/") ? storedPath.substring(1) : storedPath;
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
            throw new ResourceNotFoundException("Media not found");
        }

        try {
            Resource resource = new UrlResource(normalizedFile.toUri());
            if (!resource.exists()) {
                throw new ResourceNotFoundException("Media not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Media not found");
        }
    }

    private User requireAuthenticatedUser() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Validate post creation data
     * @param dto the post creation data
     * @throws BadRequestException if validation fails
     */
    private void validatePostData(CreatePostDto dto) {
        if (dto.postContent == null || dto.postContent.trim().isEmpty()) {
            throw new BadRequestException("Post content cannot be empty");
        }
        if (dto.postTitle == null || dto.postTitle.trim().isEmpty()) {
            throw new BadRequestException("Post title cannot be empty");
        }
        if (dto.postSubject == null || dto.postSubject.length == 0) {
            throw new BadRequestException("Post must have at least one subject");
        }
    }

    /**
     * Upload media files and return comma-separated URLs
     * @param files the files to upload
     * @return comma-separated file URLs, or empty string if no files
     */
    private String uploadMediaFiles(org.springframework.web.multipart.MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return "";
        }

        List<String> uploadedUrls = new java.util.ArrayList<>();
        for (org.springframework.web.multipart.MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String url = fileUploadUtil.upload(file);
                uploadedUrls.add(url);
            }
        }

        return String.join(",", uploadedUrls);
    }

    /**
     * Convert Post entity to PostDto
     * @param post the post entity
     * @return PostDto
     */
    private PostDto convertToDto(Post post) {
        List<CommentDto> comments = (post.getComments() == null)
            ? List.of()
            : post.getComments().stream().map(CommentMapper::toDto).toList();

        String[] mediaUrls = (post.getMediaUrl() != null && !post.getMediaUrl().isEmpty())
            ? post.getMediaUrl().split(",")
            : new String[0];

        PostDto dto = new PostDto(
            post.getTitle(),
            post.getContent(),
            post.getSubject().split(","),
            post.getVisibility(),
            mediaUrls
        );
        dto.setId(post.getId());
        dto.setCreatorUsername(post.getCreator().getUsername());
        dto.setComments(comments);
        return dto;
    }

    private void deleteExistingMedia(String mediaUrl) {
        if (mediaUrl == null || mediaUrl.isBlank()) {
            return;
        }
        String[] paths = mediaUrl.split(",");
        for (String stored : paths) {
            Path resolved = resolveMediaPath(stored.trim());
            if (resolved != null) {
                try {
                    fileUploadUtil.delete(resolved.toString());
                } catch (RuntimeException ignored) {
                    // Best-effort cleanup; continue deleting remaining files
                }
            }
        }
    }

    private Path resolveMediaPath(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            return null;
        }

        String relative = storedPath.startsWith("/") ? storedPath.substring(1) : storedPath;
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
            return null;
        }
        return normalizedFile;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String[] normalizeSubjects(String[] subjects) {
        if (subjects == null) {
            return new String[0];
        }
        return java.util.Arrays.stream(subjects)
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
    }
}
