package com.dlats.repository;

import com.dlats.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByApplication_ApplicationIdOrderByUploadedAtAsc(Integer applicationId);
    List<Document> findByVerificationStatusIgnoreCaseOrderByUploadedAtAsc(String verificationStatus);
}
