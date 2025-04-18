package com.lms.LeaveManagementSystem.repository;

import com.lms.LeaveManagementSystem.entity.LeaveHistory;
import com.lms.LeaveManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveHistoryRepository extends JpaRepository<LeaveHistory, Long> {
    List<LeaveHistory> findByEmployee(User employee);
}
