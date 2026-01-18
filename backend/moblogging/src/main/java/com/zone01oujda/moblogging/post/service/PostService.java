package com.zone01oujda.moblogging.post.service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
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
            
            User user = userRepository.findByUsernameOrEmail(details.getUsername()).orElseThrow(() -> new InternalError("error finding user"));
            String urls = "";
            if (dto.multipartFiles != null && dto.multipartFiles.length != 0){
                for (int i=0;i<dto.multipartFiles.length;i++) {
                    String fileName = fileUploadUtil.upload(dto.multipartFiles[i]);
                    urls+= fileName;
                    if (i!= dto.multipartFiles.length -1 ) {
                        urls+=",";
                    }
                    System.out.println(urls);
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

            Post post = new Post(String.join(",", dto.postSubject) ,dto.postContent,urls, dto.postVisibility, dto.postTitle);
            post.setCreator(user);
            post.setMediaUrl(urls);
            postRepository.save(post);
    
            return new PostDto(post.getTitle(), post.getContent(),dto.postSubject, post.getVisibility(), urls.split(","));
        }
        throw new AccessDeniedException("User not authenticated");
    }


    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post Not Found") );
        return new PostDto(post.getTitle(), post.getContent(), post.getSubject().split(","), post.getVisibility(), post.getMediaUrl().split(","));
    }
}
