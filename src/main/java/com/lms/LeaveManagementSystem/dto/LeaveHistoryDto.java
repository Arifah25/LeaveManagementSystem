package com.lms.LeaveManagementSystem.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LeaveHistoryDto {
    private Long id;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String leaveType;
    private String status;
    private String notes; // Additional comments or remarks
}
