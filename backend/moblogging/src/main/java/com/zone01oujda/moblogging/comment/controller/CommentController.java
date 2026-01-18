package com.zone01oujda.moblogging.comment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.comment.dto.CommentDto;
import com.zone01oujda.moblogging.comment.dto.CreateCommentDto;
import com.zone01oujda.moblogging.comment.service.CommentService;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("/comment")
public class CommentController {
    
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping("/create")
    public CommentDto createComment(@Valid @RequestBody  CreateCommentDto entity) {        
        return commentService.create(entity);
    }

    @GetMapping("/{commentId}")
    public CommentDto getMethodName(@PathVariable("commentId") Long param) {
        return commentService.getComments(param);
    }
    
    
    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommentDto updateComment(@ModelAttribute CommentDto req) {
        return commentService.create(null);
    }

    
}
