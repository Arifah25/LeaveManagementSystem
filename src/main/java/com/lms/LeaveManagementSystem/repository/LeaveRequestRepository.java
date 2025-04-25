package com.lms.LeaveManagementSystem.repository;

import com.lms.LeaveManagementSystem.entity.LeaveRequest;
import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee(User employee);

    List<LeaveRequest> findByStatus(LeaveStatus status);

    List<LeaveRequest> findByStatusIn(List<LeaveStatus> statuses);

    List<LeaveRequest> findByStatusAndManager(LeaveStatus status, User manager);
}
