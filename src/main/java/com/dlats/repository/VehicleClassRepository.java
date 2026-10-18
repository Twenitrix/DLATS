package com.dlats.repository;

import com.dlats.model.VehicleClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleClassRepository extends JpaRepository<VehicleClass, Integer> {
    Optional<VehicleClass> findByClassCodeIgnoreCase(String classCode);
}
