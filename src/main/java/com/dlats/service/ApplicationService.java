package com.dlats.service;

import com.dlats.dto.ApplicationDtos.*;
import com.dlats.exception.CustomException;
import com.dlats.model.*;
import com.dlats.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final VehicleClassRepository vehicleClassRepository;
    private final ApplicationStatusRepository statusRepository;
    private final DocumentRepository documentRepository;
    private final DrivingTestRepository drivingTestRepository;
    private final LicenseRepository licenseRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              VehicleClassRepository vehicleClassRepository,
                              ApplicationStatusRepository statusRepository,
                              DocumentRepository documentRepository,
                              DrivingTestRepository drivingTestRepository,
                              LicenseRepository licenseRepository,
                              UserRepository userRepository,
                              AuthService authService) {
        this.applicationRepository = applicationRepository;
        this.vehicleClassRepository = vehicleClassRepository;
        this.statusRepository = statusRepository;
        this.documentRepository = documentRepository;
        this.drivingTestRepository = drivingTestRepository;
        this.licenseRepository = licenseRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public List<VehicleClassDto> getAllVehicleClasses() {
        return vehicleClassRepository.findAll().stream()
                .map(vc -> new VehicleClassDto(vc.getClassId(), vc.getClassCode(), vc.getClassName(), vc.getDescription()))
                .toList();
    }

    @Transactional
    public ApplicationFullDetailsDto submitApplication(CreateApplicationRequest req) {
        User user = authService.getCurrentApplicantOrDefault();

        // Update user profile fields submitted in the application form
        if (req.fullName() != null && !req.fullName().isBlank()) user.setFullName(req.fullName().trim());
        if (req.dateOfBirth() != null) user.setDateOfBirth(req.dateOfBirth());
        if (req.gender() != null && !req.gender().isBlank()) user.setGender(req.gender().trim());
        if (req.phone() != null && !req.phone().isBlank()) user.setPhone(req.phone().trim());
        if (req.address() != null && !req.address().isBlank()) user.setAddress(req.address().trim());
        if (req.city() != null && !req.city().isBlank()) user.setCity(req.city().trim());
        if (req.state() != null && !req.state().isBlank()) user.setState(req.state().trim());
        if (req.pincode() != null && !req.pincode().isBlank()) user.setPincode(req.pincode().trim());
        user = userRepository.save(user);

        String code = (req.classCode() != null && !req.classCode().isBlank()) ? req.classCode().trim() : "LMV";
        VehicleClass vehicleClass = vehicleClassRepository.findByClassCodeIgnoreCase(code)
                .orElseGet(() -> vehicleClassRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> CustomException.badRequest("Vehicle class not found")));

        String appType = (req.applicationType() != null && !req.applicationType().isBlank())
                ? req.applicationType().trim()
                : "New Driving License";

        String appNo = generateNextApplicationNo();

        Application application = new Application(
                appNo,
                user,
                vehicleClass,
                appType,
                LocalDate.now(),
                "Application Submitted"
        );
        application = applicationRepository.save(application);

        // Initial status history entry
        ApplicationStatus initialStatus = new ApplicationStatus(
                application,
                null,
                "Application Submitted",
                "Application submitted successfully"
        );
        statusRepository.save(initialStatus);

        // Create default or custom document entries for this application
        String aadhaarFile = (req.aadhaarDocName() != null && !req.aadhaarDocName().isBlank())
                ? "uploads/" + req.aadhaarDocName().trim()
                : "uploads/aadhaar_" + application.getApplicationId() + ".pdf";
        String addressFile = (req.addressDocName() != null && !req.addressDocName().isBlank())
                ? "uploads/" + req.addressDocName().trim()
                : "uploads/address_" + application.getApplicationId() + ".pdf";
        String ageFile = (req.ageDocName() != null && !req.ageDocName().isBlank())
                ? "uploads/" + req.ageDocName().trim()
                : "uploads/age_" + application.getApplicationId() + ".pdf";

        documentRepository.save(new Document(application, "Aadhaar Card", aadhaarFile, "Pending", "Document submitted for verification", null, null));
        documentRepository.save(new Document(application, "Address Proof", addressFile, "Pending", "Document submitted for verification", null, null));
        documentRepository.save(new Document(application, "Age Proof", ageFile, "Pending", "Document submitted for verification", null, null));

        return buildFullDetails(application);
    }

    public List<ApplicationSummaryDto> getMyApplications() {
        User user = authService.getCurrentApplicantOrDefault();
        return applicationRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(ApplicationService::toSummaryDto)
                .toList();
    }

    public ApplicationFullDetailsDto getApplicationByNo(String applicationNo) {
        if (applicationNo == null || applicationNo.isBlank()) {
            User user = authService.getCurrentApplicantOrDefault();
            Application latest = applicationRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                    .stream().findFirst()
                    .orElseGet(() -> applicationRepository.findAllByOrderByCreatedAtDesc().stream().findFirst()
                            .orElseThrow(() -> CustomException.notFound("No applications found")));
            return buildFullDetails(latest);
        }
        Application application = applicationRepository.findByApplicationNoIgnoreCase(applicationNo.trim())
                .orElseThrow(() -> CustomException.notFound("Application not found with number: " + applicationNo));
        return buildFullDetails(application);
    }

    @Transactional
    public DocumentDto uploadDocument(String applicationNo, String documentType, String fileName) {
        Application application = applicationRepository.findByApplicationNoIgnoreCase(applicationNo.trim())
                .orElseThrow(() -> CustomException.notFound("Application not found: " + applicationNo));

        String type = (documentType != null && !documentType.isBlank()) ? documentType.trim() : "Identity Proof";
        String path = "uploads/" + ((fileName != null && !fileName.isBlank()) ? fileName.trim() : "doc_" + System.currentTimeMillis() + ".pdf");

        Document doc = new Document(application, type, path, "Pending", "Uploaded by applicant", null, null);
        doc = documentRepository.save(doc);

        return toDocumentDto(doc);
    }

    public ApplicationFullDetailsDto buildFullDetails(Application app) {
        VehicleClass vc = app.getVehicleClass();
        VehicleClassDto vcDto = new VehicleClassDto(vc.getClassId(), vc.getClassCode(), vc.getClassName(), vc.getDescription());

        List<StatusHistoryDto> history = statusRepository
                .findByApplication_ApplicationIdOrderByUpdatedAtAsc(app.getApplicationId())
                .stream()
                .map(s -> new StatusHistoryDto(
                        s.getStatusId(),
                        s.getStatus(),
                        s.getRemarks(),
                        s.getAdmin() != null ? s.getAdmin().getAdminName() : "System / Applicant",
                        s.getUpdatedAt()
                ))
                .toList();

        List<DocumentDto> docs = documentRepository
                .findByApplication_ApplicationIdOrderByUploadedAtAsc(app.getApplicationId())
                .stream()
                .map(ApplicationService::toDocumentDto)
                .toList();

        List<DrivingTestDto> tests = drivingTestRepository
                .findByApplication_ApplicationIdOrderByTestIdDesc(app.getApplicationId())
                .stream()
                .map(t -> new DrivingTestDto(
                        t.getTestId(),
                        t.getTestDate(),
                        t.getTestTime(),
                        t.getTestLocation(),
                        t.getResult(),
                        t.getRemarks()
                ))
                .toList();

        LicenseDto licenseDto = licenseRepository
                .findByApplication_ApplicationId(app.getApplicationId())
                .map(l -> new LicenseDto(
                        l.getLicenseId(),
                        l.getLicenseNumber(),
                        l.getIssueDate(),
                        l.getExpiryDate(),
                        l.getLicenseStatus()
                ))
                .orElse(null);

        return new ApplicationFullDetailsDto(
                app.getApplicationId(),
                app.getApplicationNo(),
                app.getApplicationType(),
                app.getApplicationDate(),
                app.getCurrentStatus(),
                UserService.toProfileDto(app.getUser()),
                vcDto,
                history,
                docs,
                tests,
                licenseDto
        );
    }

    public static DocumentDto toDocumentDto(Document d) {
        return new DocumentDto(
                d.getDocumentId(),
                d.getDocumentType(),
                d.getDocumentPath(),
                d.getVerificationStatus(),
                d.getRemarks(),
                d.getVerifiedBy() != null ? d.getVerifiedBy().getAdminName() : null,
                d.getUploadedAt(),
                d.getVerifiedAt()
        );
    }

    public static ApplicationSummaryDto toSummaryDto(Application a) {
        return new ApplicationSummaryDto(
                a.getApplicationId(),
                a.getApplicationNo(),
                a.getUser().getFullName(),
                a.getUser().getEmail(),
                a.getApplicationType(),
                a.getVehicleClass().getClassCode(),
                a.getVehicleClass().getClassName(),
                a.getApplicationDate(),
                a.getCurrentStatus()
        );
    }

    private String generateNextApplicationNo() {
        long count = applicationRepository.count();
        long nextNum = 101 + count;
        String candidate = String.format("DL2026%05d", nextNum);
        while (applicationRepository.findByApplicationNoIgnoreCase(candidate).isPresent()) {
            nextNum++;
            candidate = String.format("DL2026%05d", nextNum);
        }
        return candidate;
    }
}
