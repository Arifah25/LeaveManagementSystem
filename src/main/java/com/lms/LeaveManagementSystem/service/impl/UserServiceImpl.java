package com.lms.LeaveManagementSystem.service.impl;

import com.lms.LeaveManagementSystem.dto.DepartmentDto;
import com.lms.LeaveManagementSystem.dto.UserDto;
import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.repository.UserRepository;
import com.lms.LeaveManagementSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());
            if (user.getDepartment() != null) {
                DepartmentDto deptDto = new DepartmentDto();
                deptDto.setId(user.getDepartment().getId());
                deptDto.setName(user.getDepartment().getName());
                deptDto.setDescription(user.getDepartment().getDescription());
                dto.setDepartment(deptDto);
            }
            return dto;
        }).collect(Collectors.toList());
    }
}