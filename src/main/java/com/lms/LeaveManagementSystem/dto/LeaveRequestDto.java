package com.lms.LeaveManagementSystem.dto;

import lombok.Data;
import java.time.LocalDate;

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
    private String status;
    private String reason; // The employee's reason for leave
}
