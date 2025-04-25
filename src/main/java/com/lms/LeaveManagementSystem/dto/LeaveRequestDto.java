package com.lms.LeaveManagementSystem.dto;

import lombok.Data;
import java.time.LocalDate;

import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.enums.LeaveStatus;
import com.lms.LeaveManagementSystem.enums.LeaveType;
import com.lms.LeaveManagementSystem.enums.TimeType;

@Data
public class LeaveRequestDto {
    private Long id;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private TimeType timeType; // Optional: If the leave is for a specific time of day
    private LeaveType leaveType;
    private LeaveStatus status;
    private String reason; // The employee's reason for leave
    private ManagerDto manager; // The manager who approved or rejected the leave request
}
