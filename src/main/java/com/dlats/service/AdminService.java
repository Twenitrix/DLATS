package com.dlats.service;

import com.dlats.dto.AdminDtos.*;
import com.dlats.dto.ApplicationDtos.*;
import com.dlats.exception.CustomException;
import com.dlats.model.*;
import com.dlats.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AdminService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusRepository statusRepository;
    private final DocumentRepository documentRepository;
    private final DrivingTestRepository drivingTestRepository;
    private final LicenseRepository licenseRepository;
    private final ApplicationService applicationService;
    private final AuthService authService;

    public AdminService(ApplicationRepository applicationRepository,
                        ApplicationStatusRepository statusRepository,
                        DocumentRepository documentRepository,
                        DrivingTestRepository drivingTestRepository,
                        LicenseRepository licenseRepository,
                        ApplicationService applicationService,
                        AuthService authService) {
        this.applicationRepository = applicationRepository;
        this.statusRepository = statusRepository;
        this.documentRepository = documentRepository;
        this.drivingTestRepository = drivingTestRepository;
        this.licenseRepository = licenseRepository;
        this.applicationService = applicationService;
        this.authService = authService;
    }

    public DashboardSummaryDto getDashboardSummary(String searchQuery) {
        List<Application> allApps = applicationRepository.findAllByOrderByCreatedAtDesc();

        long total = allApps.size();
        long approved = allApps.stream()
                .filter(a -> "License Issued".equalsIgnoreCase(a.getCurrentStatus())
                          || "Approved".equalsIgnoreCase(a.getCurrentStatus()))
                .count();
        long rejected = allApps.stream()
                .filter(a -> "Rejected".equalsIgnoreCase(a.getCurrentStatus()))
                .count();
        long pending = total - approved - rejected;

        List<Application> filtered = (searchQuery != null && !searchQuery.isBlank())
                ? applicationRepository.findByApplicationNoContainingIgnoreCaseOrUser_FullNameContainingIgnoreCaseOrderByCreatedAtDesc(
                        searchQuery.trim(), searchQuery.trim())
                : allApps;

        List<ApplicationSummaryDto> summaries = filtered.stream()
                .map(ApplicationService::toSummaryDto)
                .toList();

        return new DashboardSummaryDto(total, pending, approved, rejected, summaries);
    }

    @Transactional
    public ApplicationFullDetailsDto moveToNextStage(String applicationNo, NextStageRequest req) {
        Application app = applicationRepository.findByApplicationNoIgnoreCase(applicationNo.trim())
                .orElseThrow(() -> CustomException.notFound("Application not found: " + applicationNo));

        Admin admin = authService.getCurrentAdminOrDefault();
        String current = app.getCurrentStatus();

        if ("Rejected".equalsIgnoreCase(current)) {
            throw CustomException.badRequest("Cannot advance a rejected application");
        }
        if ("License Issued".equalsIgnoreCase(current)) {
            throw CustomException.badRequest("Application has already reached final stage (License Issued)");
        }

        String nextStage;
        String defaultRemarks;

        if ("Application Submitted".equalsIgnoreCase(current)) {
            nextStage = "Document Verification";
            defaultRemarks = "Application is under document verification";
        } else if ("Document Verification".equalsIgnoreCase(current) || "Application Verification".equalsIgnoreCase(current)) {
            nextStage = "Driving Test";
            defaultRemarks = "Documents verified. Application moved to driving test stage";

            // Mark all pending documents as Verified automatically when moving past verification
            List<Document> docs = documentRepository.findByApplication_ApplicationIdOrderByUploadedAtAsc(app.getApplicationId());
            for (Document doc : docs) {
                if ("Pending".equalsIgnoreCase(doc.getVerificationStatus())) {
                    doc.setVerificationStatus("Verified");
                    doc.setRemarks("Verified during stage progression");
                    doc.setVerifiedBy(admin);
                    doc.setVerifiedAt(LocalDateTime.now());
                    documentRepository.save(doc);
                }
            }

            // Ensure a DrivingTest record exists
            List<DrivingTest> existingTests = drivingTestRepository.findByApplication_ApplicationIdOrderByTestIdDesc(app.getApplicationId());
            if (existingTests.isEmpty()) {
                LocalDate tDate = (req != null && req.testDate() != null) ? req.testDate() : LocalDate.now().plusDays(5);
                LocalTime tTime = (req != null && req.testTime() != null) ? req.testTime() : LocalTime.of(10, 30);
                String tLoc = (req != null && req.testLocation() != null && !req.testLocation().isBlank())
                        ? req.testLocation().trim()
                        : "RTO " + (app.getUser().getCity() != null ? app.getUser().getCity() : "Mumbai");
                DrivingTest test = new DrivingTest(app, tDate, tTime, tLoc, "Scheduled", "Driving test scheduled");
                drivingTestRepository.save(test);
            }
        } else {
            // Moving from Driving Test -> License Issued
            nextStage = "License Issued";
            defaultRemarks = "Driving test passed. Driving License issued successfully";

            // Mark latest driving test as Passed
            List<DrivingTest> tests = drivingTestRepository.findByApplication_ApplicationIdOrderByTestIdDesc(app.getApplicationId());
            if (!tests.isEmpty()) {
                DrivingTest latest = tests.get(0);
                latest.setResult("Passed");
                latest.setRemarks("Applicant passed practical driving test");
                drivingTestRepository.save(latest);
            }

            // Generate License record if not already present
            if (licenseRepository.findByApplication_ApplicationId(app.getApplicationId()).isEmpty()) {
                String licNo = String.format("MH01DL2026%04d", 1000 + app.getApplicationId());
                LocalDate issueDate = LocalDate.now();
                LocalDate expiryDate = issueDate.plusYears(20);
                License license = new License(app, licNo, issueDate, expiryDate, "Active");
                licenseRepository.save(license);
            }
        }

        String remarks = (req != null && req.remarks() != null && !req.remarks().isBlank())
                ? req.remarks().trim()
                : defaultRemarks;

        app.setCurrentStatus(nextStage);
        app = applicationRepository.save(app);

        ApplicationStatus statusEntry = new ApplicationStatus(app, admin, nextStage, remarks);
        statusRepository.save(statusEntry);

        return applicationService.buildFullDetails(app);
    }

    @Transactional
    public ApplicationFullDetailsDto rejectApplication(String applicationNo, RejectApplicationRequest req) {
        Application app = applicationRepository.findByApplicationNoIgnoreCase(applicationNo.trim())
                .orElseThrow(() -> CustomException.notFound("Application not found: " + applicationNo));

        String reason = (req != null && req.reason() != null && !req.reason().isBlank())
                ? req.reason().trim()
                : "Application rejected by RTO Admin";

        Admin admin = authService.getCurrentAdminOrDefault();

        app.setCurrentStatus("Rejected");
        app = applicationRepository.save(app);

        ApplicationStatus statusEntry = new ApplicationStatus(app, admin, "Rejected", reason);
        statusRepository.save(statusEntry);

        return applicationService.buildFullDetails(app);
    }

    @Transactional
    public DocumentDto verifyDocument(Integer documentId, VerifyDocumentRequest req) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> CustomException.notFound("Document not found: " + documentId));

        Admin admin = authService.getCurrentAdminOrDefault();

        String newStatus = (req != null && req.verificationStatus() != null && !req.verificationStatus().isBlank())
                ? req.verificationStatus().trim()
                : "Verified";
        String remarks = (req != null && req.remarks() != null && !req.remarks().isBlank())
                ? req.remarks().trim()
                : ("Verified".equalsIgnoreCase(newStatus) ? "Document verified successfully" : "Document verification failed");

        doc.setVerificationStatus(newStatus);
        doc.setRemarks(remarks);
        doc.setVerifiedBy(admin);
        doc.setVerifiedAt(LocalDateTime.now());
        doc = documentRepository.save(doc);

        return ApplicationService.toDocumentDto(doc);
    }

    @Transactional
    public ApplicationFullDetailsDto scheduleOrUpdateDrivingTest(String applicationNo, ScheduleDrivingTestRequest req) {
        Application app = applicationRepository.findByApplicationNoIgnoreCase(applicationNo.trim())
                .orElseThrow(() -> CustomException.notFound("Application not found: " + applicationNo));

        List<DrivingTest> existing = drivingTestRepository.findByApplication_ApplicationIdOrderByTestIdDesc(app.getApplicationId());
        DrivingTest test = existing.isEmpty() ? new DrivingTest() : existing.get(0);

        test.setApplication(app);
        test.setTestDate(req.testDate() != null ? req.testDate() : LocalDate.now().plusDays(3));
        test.setTestTime(req.testTime() != null ? req.testTime() : LocalTime.of(10, 30));
        test.setTestLocation(req.testLocation() != null && !req.testLocation().isBlank() ? req.testLocation().trim() : "RTO Mumbai");
        test.setResult(req.result() != null && !req.result().isBlank() ? req.result().trim() : "Scheduled");
        test.setRemarks(req.remarks() != null && !req.remarks().isBlank() ? req.remarks().trim() : "Driving test updated");

        drivingTestRepository.save(test);
        return applicationService.buildFullDetails(app);
    }
}
