package com.lms.LeaveManagementSystem.controller;

import com.lms.LeaveManagementSystem.dto.LeaveRequestDto;
import com.lms.LeaveManagementSystem.dto.ReportDto;
import com.lms.LeaveManagementSystem.dto.UserDto;
import com.lms.LeaveManagementSystem.service.LeaveService;
import com.lms.LeaveManagementSystem.service.ReportService;
import com.lms.LeaveManagementSystem.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
@Tag(name = "Manager Controller", description = "Manager operations")
public class ManagerController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @GetMapping("/leave-requests")
    @Operation(summary = "Get leave requests for approval", description = "Fetches leave requests pending for manager approval")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LeaveRequestDto>> getLeaveRequestsForApproval() {
        List<LeaveRequestDto> leaveRequests = leaveService.getLeaveRequestsForManager();
        return ResponseEntity.ok(leaveRequests);
    }

    @PostMapping("/leave-requests/{leaveRequestId}/approve")
    @Operation(summary = "Approve leave request", description = "Allows a manager to approve a leave request")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> approveLeaveRequest(@PathVariable Long id) {
        leaveService.managerApproveLeave(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reject leave request", description = "Allows a manager to reject a leave request")
    @PostMapping("/leave-requests/{leaveRequestId}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> rejectLeaveRequest(@PathVariable Long id) {
        leaveService.managerRejectLeave(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report")
    @Operation(summary = "Get manager report", description = "Fetches a report for the manager")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ReportDto> getManagerReport() {
        ReportDto report = reportService.getManagerReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/employees")
    @Operation(summary = "Get employees under manager", description = "Fetches a list of employees under the manager's supervision")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<UserDto>> getEmployeesUnderManager() {
        List<UserDto> employees = userService.getEmployeesUnderManager();
        return ResponseEntity.ok(employees);
    }
}
