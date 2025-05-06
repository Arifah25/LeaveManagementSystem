package com.lms.LeaveManagementSystem.controller;

import com.lms.LeaveManagementSystem.dto.LeaveRequestDto;
import com.lms.LeaveManagementSystem.service.LeaveService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave Controller", description = "Leave management operations")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @GetMapping("/{leaveRequestId}")
    @Operation(summary = "Get leave request by ID", description = "Fetches a leave request by its ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<LeaveRequestDto> getLeaveRequestById(@PathVariable Long leaveRequestId) {
        LeaveRequestDto leaveRequest = leaveService.getLeaveRequestById(leaveRequestId);
        return ResponseEntity.ok(leaveRequest);
    }

    @DeleteMapping("/{leaveRequestId}")
    @Operation(summary = "Cancel leave request", description = "Allows an employee to cancel a leave request")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> cancelLeaveRequest(@PathVariable Long leaveRequestId) {
        leaveService.cancelLeaveRequest(leaveRequestId);
        return ResponseEntity.ok().build();
    }
}
