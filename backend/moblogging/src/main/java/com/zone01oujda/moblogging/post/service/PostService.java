package com.zone01oujda.moblogging.post.service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.post.dto.CreatePostDto;
import com.zone01oujda.moblogging.post.dto.PostDto;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.FileUploadUtil;

@Service
public class PostService {

    private final FileUploadUtil fileUploadUtil;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public PostService(PostRepository postRepository, UserRepository userRepository, FileUploadUtil fileUploadUtil) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.fileUploadUtil = fileUploadUtil;
    }

    

    public PostDto createPost(CreatePostDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {

            UserDetails details = (UserDetails) authentication.getPrincipal();
            
            User user = userRepository.findByUsernameOrEmail(details.getUsername()).orElseThrow();
            if (dto.multipartFiles != null && dto.multipartFiles.length != 0){
                for (int i=0;i<dto.multipartFiles.length;i++) {
                    fileUploadUtil.upload(dto.multipartFiles[i]);
                }
            }
            if (dto.postContent.trim().isEmpty()) {
                throw new RuntimeException("the content is empty");
            }
            if (dto.postTitle.trim().isEmpty()) {
                throw new RuntimeException("the content is empty");
            }
            if (dto.postSubject.length == 0) {
                throw new RuntimeException("the content is empty");
            }
            String urls = "";
            String types ="";
            Post post = new Post(String.join(",", dto.postSubject) ,dto.postContent,urls, types, dto.postVisibility, dto.postTitle);
            post.setCreator(user);
            postRepository.save(post);
    
            return new PostDto(post.getTitle(), post.getContent(),dto.postSubject, post.getVisibility(), urls.split(","));
        }
        throw new AccessDeniedException("User not authenticated");
    }
}
