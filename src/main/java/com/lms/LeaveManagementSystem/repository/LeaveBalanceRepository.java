package com.lms.LeaveManagementSystem.repository;

import com.lms.LeaveManagementSystem.entity.LeaveBalance;
import com.lms.LeaveManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByEmployee(User employee);
}