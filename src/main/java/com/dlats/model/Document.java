package com.dlats.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Integer documentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Column(name = "document_path", length = 255)
    private String documentPath;

    @Column(name = "verification_status", length = 30)
    private String verificationStatus = "Pending";

    @Column(name = "remarks", length = 255)
    private String remarks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "verified_by")
    private Admin verifiedBy;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    public Document() {}

    public Document(Application application, String documentType, String documentPath,
                    String verificationStatus, String remarks, Admin verifiedBy, LocalDateTime verifiedAt) {
        this.application = application;
        this.documentType = documentType;
        this.documentPath = documentPath;
        this.verificationStatus = verificationStatus != null ? verificationStatus : "Pending";
        this.remarks = remarks;
        this.verifiedBy = verifiedBy;
        this.uploadedAt = LocalDateTime.now();
        this.verifiedAt = verifiedAt;
    }

    public Integer getDocumentId() { return documentId; }
    public void setDocumentId(Integer documentId) { this.documentId = documentId; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Admin getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(Admin verifiedBy) { this.verifiedBy = verifiedBy; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
}
