package com.dlats.dto;

import java.time.LocalDate;

public class AuthDtos {

    public record ApplicantRegisterRequest(
        String fullName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String gender,
        String address,
        String city,
        String state,
        String pincode,
        String password
    ) {}

    public record LoginRequest(
        String email,
        String password
    ) {}

    public record AuthTokenResponse(
        String token,
        String userType,
        Integer id,
        String name,
        String email,
        String role
    ) {}

    public record UserProfileDto(
        Integer userId,
        String fullName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String gender,
        String address,
        String city,
        String state,
        String pincode
    ) {}
}
