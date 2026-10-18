package com.dlats.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AdminDtos {

    public record DashboardSummaryDto(
        long totalApplications,
        long pendingApplications,
        long approvedApplications,
        long rejectedApplications,
        List<ApplicationDtos.ApplicationSummaryDto> applications
    ) {}

    public record NextStageRequest(
        String remarks,
        LocalDate testDate,
        LocalTime testTime,
        String testLocation
    ) {}

    public record RejectApplicationRequest(
        String reason
    ) {}

    public record VerifyDocumentRequest(
        String verificationStatus,
        String remarks
    ) {}

    public record ScheduleDrivingTestRequest(
        LocalDate testDate,
        LocalTime testTime,
        String testLocation,
        String result,
        String remarks
    ) {}
}
