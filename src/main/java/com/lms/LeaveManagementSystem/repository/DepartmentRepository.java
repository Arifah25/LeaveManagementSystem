package com.lms.LeaveManagementSystem.repository;

import com.lms.LeaveManagementSystem.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);

    Optional<Department> findById(Long id);

    boolean existsByName(String name);

    boolean existsById(Long id);

    void deleteById(Long id);

    void deleteByName(String name);

    Optional<Department> findByDescription(String description);

    boolean existsByDescription(String description);

    void deleteByDescription(String description);
}
