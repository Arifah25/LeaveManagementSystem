package com.lms.LeaveManagementSystem.controller;

import com.lms.LeaveManagementSystem.dto.LeaveRequestDto;
import com.lms.LeaveManagementSystem.dto.UserDto;
import com.lms.LeaveManagementSystem.service.LeaveService;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Controller", description = "Admin operations")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private LeaveService leaveService;

    @Operation(summary = "Get all users", description = "Fetches a list of all users in the system")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/leave-requests")
    @Operation(summary = "Get leave requests for final approval", description = "Fetches leave requests pending for admin approval")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LeaveRequestDto> listForFinalApproval() {
        return leaveService.getLeaveRequestsForAdmin();
    }

    @PostMapping("/leave-requests/{id}/approve")
    public void approve(@PathVariable Long id) {
        leaveService.adminApproveLeave(id);
    }

    @PostMapping("/leave-requests/{id}/reject")
    public void reject(@PathVariable Long id) {
        leaveService.adminRejectLeave(id);
    }
    // Additional admin endpoints (e.g. managing roles, auditing etc.) can be added
    // here.
}
