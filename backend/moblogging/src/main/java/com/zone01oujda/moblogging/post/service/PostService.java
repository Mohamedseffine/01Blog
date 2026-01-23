package com.zone01oujda.moblogging.post.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.comment.dto.CommentDto;
import com.zone01oujda.moblogging.comment.util.CommentMapper;
import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.BadRequestException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.post.dto.CreatePostDto;
import com.zone01oujda.moblogging.post.dto.PostDto;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.FileUploadUtil;
import com.zone01oujda.moblogging.util.SecurityUtil;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service class for post operations
 */
@Service
public class PostService {

    private final FileUploadUtil fileUploadUtil;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final String uploadDir;

    public PostService(PostRepository postRepository, UserRepository userRepository, FileUploadUtil fileUploadUtil,
            @Value("${files.uploadDirectory}") String uploadDir) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.fileUploadUtil = fileUploadUtil;
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
        Post post = new Post(
            String.join(",", dto.postSubject),
            dto.postContent,
            mediaUrls,
            dto.postVisibility,
            dto.postTitle
        );
        post.setCreator(user);
        post.setMediaUrl(mediaUrls);
        post = postRepository.save(post);

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

        if (Boolean.TRUE.equals(post.getHidden()) && !SecurityUtil.hasRole("ADMIN")) {
            throw new ResourceNotFoundException("Post not found");
        }

        return convertToDto(post);
    }

    public Page<PostDto> getPosts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (SecurityUtil.hasRole("ADMIN")) {
            return postRepository.findAll(pageable).map(this::convertToDto);
        }
        return postRepository.findByHiddenFalse(pageable).map(this::convertToDto);
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

        if (dto.getPostTitle() != null && !dto.getPostTitle().isBlank()) {
            post.setTitle(dto.getPostTitle().trim());
        }
        if (dto.getPostContent() != null && !dto.getPostContent().isBlank()) {
            post.setContent(dto.getPostContent().trim());
        }
        if (dto.getPostSubject() != null && dto.getPostSubject().length > 0) {
            post.setSubject(String.join(",", dto.getPostSubject()));
        }
        if (dto.getPostVisibility() != null) {
            post.setPostVisibility(dto.getPostVisibility());
        }

        Post saved = postRepository.save(post);
        return convertToDto(saved);
    }

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
}
