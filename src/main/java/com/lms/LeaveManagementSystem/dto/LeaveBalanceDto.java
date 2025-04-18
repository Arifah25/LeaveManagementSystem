package com.lms.LeaveManagementSystem.dto;

import lombok.Data;

@Data
public class LeaveBalanceDto {
    private Long id;
    private Long employeeId;
    private int totalLeaves;
    private int usedLeaves;

    public int getRemainingLeaves() {
        return totalLeaves - usedLeaves;
    }
}
