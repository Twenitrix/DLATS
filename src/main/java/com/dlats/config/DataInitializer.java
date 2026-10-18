package com.dlats.config;

import com.dlats.model.*;
import com.dlats.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final VehicleClassRepository vehicleClassRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusRepository statusRepository;
    private final DocumentRepository documentRepository;
    private final DrivingTestRepository drivingTestRepository;
    private final LicenseRepository licenseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AdminRepository adminRepository,
                           VehicleClassRepository vehicleClassRepository,
                           ApplicationRepository applicationRepository,
                           ApplicationStatusRepository statusRepository,
                           DocumentRepository documentRepository,
                           DrivingTestRepository drivingTestRepository,
                           LicenseRepository licenseRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.vehicleClassRepository = vehicleClassRepository;
        this.applicationRepository = applicationRepository;
        this.statusRepository = statusRepository;
        this.documentRepository = documentRepository;
        this.drivingTestRepository = drivingTestRepository;
        this.licenseRepository = licenseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (vehicleClassRepository.count() > 0) {
            return;
        }

        // 1. Seed Users (from database/sample_data.sql + UI sample data)
        User rahul = userRepository.save(new User(
                "Rahul Sharma",
                "rahul@example.com",
                passwordEncoder.encode("rahul123"),
                "9876543210",
                LocalDate.of(2000, 5, 15),
                "Male",
                "Andheri West",
                "Mumbai",
                "Maharashtra",
                "400058"
        ));

        User priya = userRepository.save(new User(
                "Priya Patil",
                "priya@example.com",
                passwordEncoder.encode("priya123"),
                "9876501234",
                LocalDate.of(1999, 8, 20),
                "Female",
                "Kothrud",
                "Pune",
                "Maharashtra",
                "411038"
        ));

        User arjun = userRepository.save(new User(
                "Arjun Mehta",
                "arjun@example.com",
                passwordEncoder.encode("arjun123"),
                "9876511111",
                LocalDate.of(1998, 3, 10),
                "Male",
                "Bandra East",
                "Mumbai",
                "Maharashtra",
                "400051"
        ));

        // 2. Seed Admins
        Admin rtoAdmin = adminRepository.save(new Admin(
                "RTO Admin",
                "admin@dlats.com",
                passwordEncoder.encode("admin123"),
                "Admin"
        ));

        Admin officer = adminRepository.save(new Admin(
                "Verification Officer",
                "officer@dlats.com",
                passwordEncoder.encode("officer123"),
                "Officer"
        ));

        // 3. Seed Vehicle Classes
        VehicleClass mcwog = vehicleClassRepository.save(new VehicleClass("MCWOG", "Motorcycle Without Gear", "Two wheeler without gear"));
        VehicleClass mcwg = vehicleClassRepository.save(new VehicleClass("MCWG", "Motorcycle With Gear", "Two wheeler with gear"));
        VehicleClass lmv = vehicleClassRepository.save(new VehicleClass("LMV", "Light Motor Vehicle", "Cars and other light motor vehicles"));
        VehicleClass hmv = vehicleClassRepository.save(new VehicleClass("HMV", "Heavy Motor Vehicle", "Heavy motor vehicles"));

        // 4. Seed Applications
        Application app1 = applicationRepository.save(new Application(
                "DL202600101",
                rahul,
                lmv,
                "New Driving License",
                LocalDate.of(2026, 9, 10),
                "Document Verification"
        ));

        Application app2 = applicationRepository.save(new Application(
                "DL202600102",
                priya,
                mcwg,
                "New Driving License",
                LocalDate.of(2026, 9, 11),
                "License Issued"
        ));

        Application app3 = applicationRepository.save(new Application(
                "DL202600103",
                arjun,
                lmv,
                "New Driving License",
                LocalDate.of(2026, 9, 12),
                "Rejected"
        ));

        // 5. Seed Application Status History
        ApplicationStatus s1 = new ApplicationStatus(app1, null, "Application Submitted", "Application submitted successfully");
        s1.setUpdatedAt(LocalDateTime.of(2026, 9, 10, 10, 0));
        statusRepository.save(s1);

        ApplicationStatus s2 = new ApplicationStatus(app1, rtoAdmin, "Document Verification", "Application is under document verification");
        s2.setUpdatedAt(LocalDateTime.of(2026, 9, 12, 11, 30));
        statusRepository.save(s2);

        statusRepository.save(new ApplicationStatus(app2, null, "Application Submitted", "Application submitted successfully"));
        statusRepository.save(new ApplicationStatus(app2, officer, "Document Verification", "All documents verified"));
        statusRepository.save(new ApplicationStatus(app2, rtoAdmin, "Driving Test", "Driving test scheduled and passed"));
        statusRepository.save(new ApplicationStatus(app2, rtoAdmin, "License Issued", "Driving license MH01DL20261001 issued"));

        statusRepository.save(new ApplicationStatus(app3, null, "Application Submitted", "Application submitted successfully"));
        statusRepository.save(new ApplicationStatus(app3, rtoAdmin, "Rejected", "Required document could not be verified"));

        // 6. Seed Documents
        documentRepository.save(new Document(app1, "Aadhaar Card", "uploads/aadhaar_1.pdf", "Verified", "Identity verified successfully", officer, LocalDateTime.of(2026, 9, 12, 11, 30)));
        documentRepository.save(new Document(app1, "Address Proof", "uploads/address_1.pdf", "Verified", "Address proof verified", officer, LocalDateTime.of(2026, 9, 12, 11, 35)));
        documentRepository.save(new Document(app1, "Age Proof", "uploads/age_1.pdf", "Pending", "Awaiting verification", null, null));

        documentRepository.save(new Document(app2, "Aadhaar Card", "uploads/aadhaar_2.pdf", "Verified", "Identity verified", officer, LocalDateTime.of(2026, 9, 13, 14, 0)));
        documentRepository.save(new Document(app2, "Address Proof", "uploads/address_2.pdf", "Verified", "Address verified", officer, LocalDateTime.of(2026, 9, 13, 14, 5)));
        documentRepository.save(new Document(app2, "Age Proof", "uploads/age_2.pdf", "Verified", "Age proof verified", officer, LocalDateTime.of(2026, 9, 13, 14, 10)));

        documentRepository.save(new Document(app3, "Aadhaar Card", "uploads/aadhaar_3.pdf", "Rejected", "Blurred scan uploaded", officer, LocalDateTime.of(2026, 9, 14, 16, 0)));

        // 7. Seed Driving Tests
        drivingTestRepository.save(new DrivingTest(app1, LocalDate.of(2026, 10, 15), LocalTime.of(10, 30), "RTO Mumbai", "Scheduled", "Driving test scheduled"));
        drivingTestRepository.save(new DrivingTest(app2, LocalDate.of(2026, 10, 10), LocalTime.of(11, 0), "RTO Pune", "Passed", "Completed test without errors"));

        // 8. Seed Issued License
        licenseRepository.save(new License(app2, "MH12DL20261002", LocalDate.of(2026, 10, 12), LocalDate.of(2046, 10, 12), "Active"));
    }
}
