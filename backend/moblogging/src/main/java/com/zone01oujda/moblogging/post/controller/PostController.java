package com.zone01oujda.moblogging.post.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.post.dto.CreatePostDto;
import com.zone01oujda.moblogging.post.dto.PostDto;
import com.zone01oujda.moblogging.post.service.PostService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/posts")
public class PostController {

    
    private final PostService postService;

    public PostController(@Valid @RequestBody PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/create", consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    public PostDto postMethodName(@Valid @ModelAttribute CreatePostDto entity) {
        
        return postService.createPost(entity);
    }
    
}
