package com.lms.LeaveManagementSystem.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class LeaveRequestDto {
    private Long id;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime; // Optional: If the leave is for a specific time of day
    private LocalTime endTime; // Optional: If the leave is for a specific time of day
    private String leaveType;
    private String status;
    private String reason; // The employee's reason for leave
}
