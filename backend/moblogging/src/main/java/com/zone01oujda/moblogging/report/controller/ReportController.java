package com.zone01oujda.moblogging.report.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createReport(@Valid @RequestBody Object reportDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Report submitted successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reports retrieved successfully", null)
        );
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<Object>> getReportById(@PathVariable("reportId") Long reportId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Report retrieved successfully", null)
        );
    }

    @PutMapping("/{reportId}/resolve")
    public ResponseEntity<ApiResponse<Object>> resolveReport(@PathVariable("reportId") Long reportId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Report resolved successfully", null)
        );
    }
    
}
