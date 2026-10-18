package com.dlats.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "license")
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "license_id")
    private Integer licenseId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @Column(name = "license_number", nullable = false, unique = true, length = 30)
    private String licenseNumber;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "license_status", length = 20)
    private String licenseStatus = "Active";

    public License() {}

    public License(Application application, String licenseNumber,
                   LocalDate issueDate, LocalDate expiryDate, String licenseStatus) {
        this.application = application;
        this.licenseNumber = licenseNumber;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.licenseStatus = licenseStatus != null ? licenseStatus : "Active";
    }

    public Integer getLicenseId() { return licenseId; }
    public void setLicenseId(Integer licenseId) { this.licenseId = licenseId; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getLicenseStatus() { return licenseStatus; }
    public void setLicenseStatus(String licenseStatus) { this.licenseStatus = licenseStatus; }
}
