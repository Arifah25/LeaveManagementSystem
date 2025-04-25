package com.lms.LeaveManagementSystem.controller;

import com.lms.LeaveManagementSystem.dto.DepartmentDto;
import com.lms.LeaveManagementSystem.dto.LeaveRequestDto;
import com.lms.LeaveManagementSystem.dto.UserDto;
import com.lms.LeaveManagementSystem.service.DepartmentService;
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
    @Autowired
    private DepartmentService departmentService;

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

    @PostMapping("/leave-requests/{leaveRequestId}/approve")
    @Operation(summary = "Approve leave request", description = "Approves a leave request by the employee")
    @PreAuthorize("hasRole('ADMIN')")
    public void approve(@PathVariable Long id) {
        leaveService.adminApproveLeave(id);
    }

    @PostMapping("/leave-requests/{leaveRequestId}/reject")
    @Operation(summary = "Reject leave request", description = "Rejects a leave request by the employee")
    @PreAuthorize("hasRole('ADMIN')")
    public void reject(@PathVariable Long id) {
        leaveService.adminRejectLeave(id);
    }

    @PostMapping("/departments")
    @Operation(summary = "Create a new department", description = "Creates a new department in the system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDto> createDepartment(@RequestParam String name,
            @RequestParam String description) {
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setName(name);
        departmentDto.setDescription(description);
        DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
        return ResponseEntity.ok(createdDepartment);
    }

    @GetMapping("/departments")
    @Operation(summary = "Get all departments", description = "Fetches a list of all departments in the system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/departments/{departmentId}/employees")
    @Operation(summary = "Get employees by department", description = "Fetches a list of employees in a specific department")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getEmployeesByDepartment(@PathVariable Long id) {
        List<UserDto> employees = departmentService.getTotalEmployeeByDepartment(id);
        return ResponseEntity.ok(employees);
    }

    @DeleteMapping("/departments/{departmentId}")
    @Operation(summary = "Delete a department", description = "Deletes a department from the system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

}
