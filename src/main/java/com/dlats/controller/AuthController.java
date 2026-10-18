package com.dlats.controller;

import com.dlats.dto.ApiResponse;
import com.dlats.dto.AuthDtos.*;
import com.dlats.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/applicant/register")
    public ApiResponse<AuthTokenResponse> registerApplicant(@RequestBody ApplicantRegisterRequest req) {
        return ApiResponse.ok(authService.registerApplicant(req), "Applicant registered successfully");
    }

    @PostMapping("/applicant/login")
    public ApiResponse<AuthTokenResponse> loginApplicant(@RequestBody LoginRequest req) {
        return ApiResponse.ok(authService.loginApplicant(req), "Applicant logged in successfully");
    }

    @PostMapping("/admin/login")
    public ApiResponse<AuthTokenResponse> loginAdmin(@RequestBody LoginRequest req) {
        return ApiResponse.ok(authService.loginAdmin(req), "Admin logged in successfully");
    }
}
