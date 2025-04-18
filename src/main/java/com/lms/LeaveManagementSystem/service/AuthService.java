package com.lms.LeaveManagementSystem.service;

import com.lms.LeaveManagementSystem.dto.LoginRequestDto;
import com.lms.LeaveManagementSystem.dto.RegisterRequestDto;

public interface AuthService {
    String login(LoginRequestDto loginRequestDto);

    String register(RegisterRequestDto registerRequestDto);
}
