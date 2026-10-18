package com.dlats.controller;

import com.dlats.dto.ApiResponse;
import com.dlats.dto.ApplicationDtos.*;
import com.dlats.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ApiResponse<ApplicationFullDetailsDto> submitApplication(@RequestBody CreateApplicationRequest req) {
        return ApiResponse.ok(applicationService.submitApplication(req), "Driving license application submitted successfully");
    }

    @GetMapping("/my")
    public ApiResponse<List<ApplicationSummaryDto>> getMyApplications() {
        return ApiResponse.ok(applicationService.getMyApplications(), "My applications fetched successfully");
    }

    @GetMapping("/{applicationNo}")
    public ApiResponse<ApplicationFullDetailsDto> getApplicationDetails(@PathVariable String applicationNo) {
        return ApiResponse.ok(applicationService.getApplicationByNo(applicationNo), "Application details fetched successfully");
    }

    @GetMapping("/track/{applicationNo}")
    public ApiResponse<ApplicationFullDetailsDto> trackApplication(@PathVariable String applicationNo) {
        return ApiResponse.ok(applicationService.getApplicationByNo(applicationNo), "Application tracking status fetched successfully");
    }

    @PostMapping("/{applicationNo}/documents")
    public ApiResponse<DocumentDto> uploadDocument(
            @PathVariable String applicationNo,
            @RequestBody Map<String, String> body) {
        String documentType = body.get("documentType");
        String fileName = body.get("fileName");
        return ApiResponse.ok(
                applicationService.uploadDocument(applicationNo, documentType, fileName),
                "Document uploaded successfully"
        );
    }
}
