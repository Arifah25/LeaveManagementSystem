package com.lms.LeaveManagementSystem.dto;

import lombok.Data;

@Data
public class ReportDto {
    private int totalRequests;
    private int approvedRequests;
    private int rejectedRequests;
    private int pendingRequests;
}
