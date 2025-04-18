package com.lms.LeaveManagementSystem.service;

import com.lms.LeaveManagementSystem.dto.LeaveRequestDto;
import com.lms.LeaveManagementSystem.dto.LeaveHistoryDto;
import com.lms.LeaveManagementSystem.dto.LeaveBalanceDto;
import java.util.List;

public interface LeaveService {
    LeaveRequestDto applyLeave(LeaveRequestDto leaveRequestDto);

    List<LeaveRequestDto> getLeaveRequestsForManager();

    void managerApproveLeave(Long requestId);

    void managerRejectLeave(Long requestId);

    List<LeaveRequestDto> getLeaveRequestsForAdmin();

    void adminApproveLeave(Long requestId);

    void adminRejectLeave(Long requestId);

    List<LeaveHistoryDto> getLeaveHistoryForEmployee();

    LeaveBalanceDto getLeaveBalanceForEmployee();

    LeaveRequestDto getLeaveRequestById(Long id);

    void cancelLeaveRequest(Long id);
}
