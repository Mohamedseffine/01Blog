package com.zone01oujda.moblogging.post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.util.response.ApiResponse;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getUserFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Feed retrieved successfully", null)
        );
    }

    @GetMapping("/explore")
    public ResponseEntity<ApiResponse<Object>> getExploreFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Explore feed retrieved successfully", null)
        );
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<Object>> getTrendingFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Trending feed retrieved successfully", null)
        );
    }
    
}
