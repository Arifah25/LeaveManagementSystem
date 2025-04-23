package com.lms.LeaveManagementSystem.util;

import com.lms.LeaveManagementSystem.entity.Department;
import com.lms.LeaveManagementSystem.entity.LeaveBalance;
import com.lms.LeaveManagementSystem.enums.Role;
import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.repository.DepartmentRepository;
import com.lms.LeaveManagementSystem.repository.LeaveBalanceRepository;
import com.lms.LeaveManagementSystem.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {

        // read default total leaves from properties
        @Value("${leave.default-total}")
        private int defaultTotalLeaves;

        @Autowired
        private DepartmentRepository departmentRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired
        private LeaveBalanceRepository leaveBalanceRepository;

        @PostConstruct
        @Transactional
        public void seedData() {
                // 2. Seed departments
                List<String> deptNames = List.of("Audit & Assurance", "Internal Services",
                                "Strategy, Risk & Transactions", "Tax & Legal", "Technology & Transformation");
                for (String name : deptNames) {
                        departmentRepository.findByName(name)
                                        .orElseGet(() -> departmentRepository.save(
                                                        Department.builder()
                                                                        .name(name)
                                                                        .description(name + " Department")
                                                                        .build()));
                }

                // 3. Seed users
                seedUser("admin", "admin@example.com", "password", Role.ADMIN,
                                departmentRepository.findByName("Internal Services").get(), null);
                User manager = seedUser("manager", "manager@example.com", "password", Role.MANAGER,
                                departmentRepository.findByName("Technology & Transformation").get(), null);
                seedUser("employee", "employee@example.com", "password", Role.EMPLOYEE,
                                departmentRepository.findByName("Technology & Transformation").get(), manager);
        }

        private User seedUser(String fullName,
                        String email,
                        String rawPassword,
                        Role roleType,
                        Department dept,
                        User manager) {
                if (userRepository.findByEmail(email).isPresent()) {
                        return userRepository.findByEmail(email).get(); // return existing user
                }

                User user = User.builder()
                                .fullName(fullName)
                                .email(email)
                                .password(passwordEncoder.encode(rawPassword))
                                .role(roleType)
                                .department(dept)
                                .manager(manager) // Set the manager here
                                .build();
                userRepository.save(user);

                LeaveBalance balance = LeaveBalance.builder()
                                .employee(user)
                                .totalLeaves(defaultTotalLeaves)
                                .usedLeaves(0)
                                .build();
                leaveBalanceRepository.save(balance);

                return user; // Return the created user
        }
}