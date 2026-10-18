package com.dlats.repository;

import com.dlats.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Optional<Application> findByApplicationNoIgnoreCase(String applicationNo);
    List<Application> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);
    List<Application> findAllByOrderByCreatedAtDesc();
    List<Application> findByApplicationNoContainingIgnoreCaseOrUser_FullNameContainingIgnoreCaseOrderByCreatedAtDesc(String appNo, String fullName);
    long countByCurrentStatusIgnoreCase(String status);
}
