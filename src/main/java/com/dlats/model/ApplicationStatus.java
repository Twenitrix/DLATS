package com.dlats.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "application_status")
public class ApplicationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "remarks", length = 255)
    private String remarks;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ApplicationStatus() {}

    public ApplicationStatus(Application application, Admin admin, String status, String remarks) {
        this.application = application;
        this.admin = admin;
        this.status = status;
        this.remarks = remarks;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getStatusId() { return statusId; }
    public void setStatusId(Integer statusId) { this.statusId = statusId; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
