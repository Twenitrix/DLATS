package com.dlats.repository;

import com.dlats.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Integer> {
    Optional<License> findByApplication_ApplicationId(Integer applicationId);
    Optional<License> findByLicenseNumberIgnoreCase(String licenseNumber);
}
