package com.lms.LeaveManagementSystem.service.impl;

import com.lms.LeaveManagementSystem.dto.DepartmentDto;
import com.lms.LeaveManagementSystem.dto.UserDto;
import com.lms.LeaveManagementSystem.entity.Department;
import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.repository.DepartmentRepository;
import com.lms.LeaveManagementSystem.repository.UserRepository;
import com.lms.LeaveManagementSystem.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Cacheable(cacheNames = "departments")
    @Override
    public List<DepartmentDto> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream().map(dept -> {
            DepartmentDto dto = new DepartmentDto();
            dto.setId(dept.getId());
            dto.setName(dept.getName());
            dto.setDescription(dept.getDescription());
            return dto;
        }).collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "employeesByDepartment", key = "#id")
    @Override
    public List<UserDto> getTotalEmployeeByDepartment(Long id) {
        // Fetch the department by ID
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        List<User> users = userRepository.findByDepartment(department);
        return users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole()); // Assuming Role is an enum
            return dto;
        }).collect(Collectors.toList());
    }

    @CacheEvict(cacheNames = "employeesByDepartment", allEntries = true)
    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        // Check if the department already exists
        if (departmentRepository.existsByName(departmentDto.getName())) {
            throw new RuntimeException("Department already exists");
        }

        // Create a new department entity
        Department department = new Department();
        department.setName(departmentDto.getName());
        department.setDescription(departmentDto.getDescription());

        // Save the department to the database
        Department savedDepartment = departmentRepository.save(department);

        // Convert to DTO and return
        DepartmentDto dto = new DepartmentDto();
        dto.setId(savedDepartment.getId());
        dto.setName(savedDepartment.getName());
        dto.setDescription(savedDepartment.getDescription());
        return dto;
    }

    @CacheEvict(cacheNames = "departments", allEntries = true)
    @Override
    public void deleteDepartment(Long id) {
        // Check if the department exists
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found");
        }

        // Delete the department
        departmentRepository.deleteById(id);
    }

}