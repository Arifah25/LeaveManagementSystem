package com.lms.LeaveManagementSystem.dto;

import lombok.Data;
import java.util.List;

import com.lms.LeaveManagementSystem.enums.Role;

@Data
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private DepartmentDto department;
    private UserDto manager; // This will be null for top-level managers or admins
}
