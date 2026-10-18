package com.dlats.controller;

import com.dlats.dto.AdminDtos.*;
import com.dlats.dto.ApiResponse;
import com.dlats.dto.ApplicationDtos.*;
import com.dlats.service.AdminService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardSummaryDto> getDashboard(@RequestParam(required = false) String search) {
        return ApiResponse.ok(adminService.getDashboardSummary(search), "Admin dashboard loaded successfully");
    }

    @PostMapping("/applications/{applicationNo}/next-stage")
    public ApiResponse<ApplicationFullDetailsDto> moveToNextStage(
            @PathVariable String applicationNo,
            @RequestBody(required = false) NextStageRequest req) {
        return ApiResponse.ok(adminService.moveToNextStage(applicationNo, req), "Application moved to next stage");
    }

    @PostMapping("/applications/{applicationNo}/reject")
    public ApiResponse<ApplicationFullDetailsDto> rejectApplication(
            @PathVariable String applicationNo,
            @RequestBody RejectApplicationRequest req) {
        return ApiResponse.ok(adminService.rejectApplication(applicationNo, req), "Application rejected");
    }

    @PatchMapping("/documents/{documentId}/verify")
    public ApiResponse<DocumentDto> verifyDocument(
            @PathVariable Integer documentId,
            @RequestBody VerifyDocumentRequest req) {
        return ApiResponse.ok(adminService.verifyDocument(documentId, req), "Document verification status updated");
    }

    @PostMapping("/applications/{applicationNo}/driving-test")
    public ApiResponse<ApplicationFullDetailsDto> scheduleDrivingTest(
            @PathVariable String applicationNo,
            @RequestBody ScheduleDrivingTestRequest req) {
        return ApiResponse.ok(adminService.scheduleOrUpdateDrivingTest(applicationNo, req), "Driving test updated");
    }
}
