package com.lms.LeaveManagementSystem.dto;

import com.lms.LeaveManagementSystem.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String password;
    private Role role;
    private Long departmentId; // to link the user to a department during registration
    private Long managerId; // to link the user to a manager during registration
}
