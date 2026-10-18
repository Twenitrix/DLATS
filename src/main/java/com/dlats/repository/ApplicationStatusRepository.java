package com.dlats.repository;

import com.dlats.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, Integer> {
    List<ApplicationStatus> findByApplication_ApplicationIdOrderByUpdatedAtAsc(Integer applicationId);
}
