package com.lms.LeaveManagementSystem.controller;

import com.lms.LeaveManagementSystem.dto.LeaveRequestDto;
import com.lms.LeaveManagementSystem.enums.LeaveType;
import com.lms.LeaveManagementSystem.enums.TimeType;
import com.lms.LeaveManagementSystem.dto.LeaveBalanceDto;
import com.lms.LeaveManagementSystem.service.LeaveService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<LeaveRequestDto> applyForLeave(
            @RequestParam("startDate") @DateTimeFormat(pattern = "d-M-yyyy") @Parameter(schema = @Schema(type = "string", format = "date", example = "28-4-2025")) LocalDate startDate,

            @RequestParam("endDate") @DateTimeFormat(pattern = "d-M-yyyy") @Parameter(schema = @Schema(type = "string", format = "date", example = "28-4-2025")) LocalDate endDate,

            @RequestParam("time") TimeType time,

            @RequestParam("leaveType") LeaveType leaveType,

            @RequestParam("reason") String reason) {
        LeaveRequestDto leaveRequestDto = new LeaveRequestDto();
        leaveRequestDto.setStartDate(startDate);
        leaveRequestDto.setEndDate(endDate);
        leaveRequestDto.setTimeType(time);

        leaveRequestDto.setLeaveType(leaveType);
        leaveRequestDto.setReason(reason);
        LeaveRequestDto createdLeaveRequest = leaveService.applyLeave(leaveRequestDto);
        return ResponseEntity.ok(createdLeaveRequest);
    }

    @GetMapping("/leave-history")
    @Operation(summary = "Get leave history", description = "Fetches the leave history for the employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<LeaveRequestDto>> getLeaveHistory() {
        List<LeaveRequestDto> leaveHistory = leaveService.getLeaveHistoryForEmployee();
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
