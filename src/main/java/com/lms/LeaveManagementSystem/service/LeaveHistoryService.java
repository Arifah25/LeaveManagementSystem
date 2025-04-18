package com.lms.LeaveManagementSystem.service;

import com.lms.LeaveManagementSystem.dto.LeaveHistoryDto;
import java.util.List;

public interface LeaveHistoryService {
    List<LeaveHistoryDto> getLeaveHistoryForEmployee();
}
