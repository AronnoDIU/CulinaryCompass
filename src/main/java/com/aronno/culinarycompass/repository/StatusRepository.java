package com.aronno.culinarycompass.repository;

import com.aronno.culinarycompass.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {
    Optional<Status> findByNameAndCategory(String name, String category);
    List<Status> findByCategory(String category);
    List<Status> findByCategoryAndIsActiveTrue(String category);
}
