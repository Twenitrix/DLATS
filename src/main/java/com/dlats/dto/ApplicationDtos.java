package com.dlats.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ApplicationDtos {

    public record CreateApplicationRequest(
        String fullName,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        String email,
        String address,
        String city,
        String state,
        String pincode,
        String applicationType,
        String classCode,
        String aadhaarDocName,
        String addressDocName,
        String ageDocName
    ) {}

    public record VehicleClassDto(
        Integer classId,
        String classCode,
        String className,
        String description
    ) {}

    public record StatusHistoryDto(
        Integer statusId,
        String status,
        String remarks,
        String updatedByAdmin,
        LocalDateTime updatedAt
    ) {}

    public record DocumentDto(
        Integer documentId,
        String documentType,
        String documentPath,
        String verificationStatus,
        String remarks,
        String verifiedByAdmin,
        LocalDateTime uploadedAt,
        LocalDateTime verifiedAt
    ) {}

    public record DrivingTestDto(
        Integer testId,
        LocalDate testDate,
        LocalTime testTime,
        String testLocation,
        String result,
        String remarks
    ) {}

    public record LicenseDto(
        Integer licenseId,
        String licenseNumber,
        LocalDate issueDate,
        LocalDate expiryDate,
        String licenseStatus
    ) {}

    public record ApplicationSummaryDto(
        Integer applicationId,
        String applicationNo,
        String applicantName,
        String applicantEmail,
        String applicationType,
        String classCode,
        String className,
        LocalDate applicationDate,
        String currentStatus
    ) {}

    public record ApplicationFullDetailsDto(
        Integer applicationId,
        String applicationNo,
        String applicationType,
        LocalDate applicationDate,
        String currentStatus,
        AuthDtos.UserProfileDto applicant,
        VehicleClassDto vehicleClass,
        List<StatusHistoryDto> statusHistory,
        List<DocumentDto> documents,
        List<DrivingTestDto> drivingTests,
        LicenseDto license
    ) {}
}
