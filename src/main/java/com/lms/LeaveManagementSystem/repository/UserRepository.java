package com.lms.LeaveManagementSystem.repository;

import com.lms.LeaveManagementSystem.entity.Department;
import com.lms.LeaveManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByDepartment(Department department);

    Optional<User> findById(Long id);

    void deleteById(Long id);

    void deleteByEmail(String email);

    void deleteByDepartment(Department department);

    boolean existsById(Long id);

    boolean existsByDepartment(Department department);

    boolean existsByEmail(String email);
}
