package com.dlats.controller;

import com.dlats.dto.ApiResponse;
import com.dlats.dto.ApplicationDtos.VehicleClassDto;
import com.dlats.dto.AuthDtos.UserProfileDto;
import com.dlats.service.ApplicationService;
import com.dlats.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final ApplicationService applicationService;

    public UserController(UserService userService, ApplicationService applicationService) {
        this.userService = userService;
        this.applicationService = applicationService;
    }

    @GetMapping("/users/profile")
    public ApiResponse<UserProfileDto> getProfile() {
        return ApiResponse.ok(userService.getProfile(), "Profile fetched successfully");
    }

    @PutMapping("/users/profile")
    public ApiResponse<UserProfileDto> updateProfile(@RequestBody UserProfileDto req) {
        return ApiResponse.ok(userService.updateProfile(req), "Profile updated successfully");
    }

    @GetMapping("/vehicle-classes")
    public ApiResponse<List<VehicleClassDto>> getVehicleClasses() {
        return ApiResponse.ok(applicationService.getAllVehicleClasses(), "Vehicle classes fetched successfully");
    }
}
