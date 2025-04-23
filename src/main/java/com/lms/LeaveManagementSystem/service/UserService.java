package com.lms.LeaveManagementSystem.service;

import com.lms.LeaveManagementSystem.dto.UserDto;
import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    List<UserDto> getEmployeesUnderManager();
}
