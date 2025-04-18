package com.lms.LeaveManagementSystem.controller;

import com.lms.LeaveManagementSystem.dto.LeaveRequestDto;
import com.lms.LeaveManagementSystem.dto.LeaveHistoryDto;
import com.lms.LeaveManagementSystem.dto.LeaveBalanceDto;
import com.lms.LeaveManagementSystem.service.LeaveService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Tag(name = "Employee Controller", description = "Employee operations")
public class EmployeeController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/leave-requests")
    @Operation(summary = "Apply for leave", description = "Allows an employee to apply for a leave request")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<LeaveRequestDto> applyForLeave(@RequestBody LeaveRequestDto leaveRequestDto) {
        LeaveRequestDto createdLeaveRequest = leaveService.applyLeave(leaveRequestDto);
        return ResponseEntity.ok(createdLeaveRequest);
    }

    @GetMapping("/leave-history")
    @Operation(summary = "Get leave history", description = "Fetches the leave history for the employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<LeaveHistoryDto>> getLeaveHistory() {
        List<LeaveHistoryDto> leaveHistory = leaveService.getLeaveHistoryForEmployee();
        return ResponseEntity.ok(leaveHistory);
    }

    @GetMapping("/leave-balance")
    @Operation(summary = "Get leave balance", description = "Fetches the leave balance for the employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<LeaveBalanceDto> getLeaveBalance() {
        LeaveBalanceDto leaveBalance = leaveService.getLeaveBalanceForEmployee();
        return ResponseEntity.ok(leaveBalance);
    }
}
