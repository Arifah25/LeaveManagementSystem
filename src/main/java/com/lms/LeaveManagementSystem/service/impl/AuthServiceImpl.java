package com.lms.LeaveManagementSystem.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lms.LeaveManagementSystem.dto.LoginRequestDto;
import com.lms.LeaveManagementSystem.dto.RegisterRequestDto;
import com.lms.LeaveManagementSystem.entity.LeaveBalance;
import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.repository.DepartmentRepository;
import com.lms.LeaveManagementSystem.repository.LeaveBalanceRepository;
import com.lms.LeaveManagementSystem.repository.UserRepository;
import com.lms.LeaveManagementSystem.service.AuthService;
import com.lms.LeaveManagementSystem.service.LeaveBalanceService;
import com.lms.LeaveManagementSystem.service.ReportService;
import com.lms.LeaveManagementSystem.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    // Assuming you have a UserService, LeaveBalanceService, and ReportService
    // injected here
    private final UserRepository userRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PasswordEncoder passwordEncoder; // Assuming you have a PasswordEncoder bean
    private final DepartmentRepository departmentRepository; // Assuming you have a DepartmentRepository
    private final JwtUtil jwtUtil;

    // read default total leaves from properties
    @Value("${leave.default-total}")
    private int defaultTotalLeaves;

    @Override
    public String register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User().builder()
                .fullName(registerRequestDto.getFullName())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .role(registerRequestDto.getRole())
                .department(departmentRepository.findById(registerRequestDto.getDepartmentId()).orElse(null))
                .manager(registerRequestDto.getManagerId() != null
                        ? userRepository.findById(registerRequestDto.getManagerId()).orElse(null)
                        : null)
                .build();

        User savedUser = userRepository.save(user);
        // Initialize leave balance for the user

        LeaveBalance balance = LeaveBalance.builder()
                .employee(savedUser)
                .totalLeaves(defaultTotalLeaves)
                .usedLeaves(0)
                .build();
        leaveBalanceRepository.save(balance);

        return "Registration successful";
    }

    @Override
    public String login(LoginRequestDto loginRequestDto) {
        try {
            // Instead of calling authenticate, log the request and manually check for user
            User user = userRepository.findByEmail(loginRequestDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }

            // Generate JWT token manually after verifying the user
            return jwtUtil.generateToken(loginRequestDto.getEmail());

        } catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }

}
