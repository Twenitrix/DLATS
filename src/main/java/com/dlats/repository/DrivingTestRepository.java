package com.dlats.repository;

import com.dlats.model.DrivingTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrivingTestRepository extends JpaRepository<DrivingTest, Integer> {
    List<DrivingTest> findByApplication_ApplicationIdOrderByTestIdDesc(Integer applicationId);
}
