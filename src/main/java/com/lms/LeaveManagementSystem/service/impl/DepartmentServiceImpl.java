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
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    // Cache the list of all departments
    @Override
    @Cacheable(cacheNames = "departments")
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

    // Cache the employees for a given department ID
    @Override
    @Cacheable(cacheNames = "employeesByDepartment", key = "#id")
    public List<UserDto> getTotalEmployeeByDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        List<User> users = userRepository.findByDepartment(department);
        return users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());
            return dto;
        }).collect(Collectors.toList());
    }

    // Evict both department list and employeesByDepartment caches when creating
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "departments", allEntries = true),
            @CacheEvict(cacheNames = "employeesByDepartment", allEntries = true)
    })
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        if (departmentRepository.existsByName(departmentDto.getName())) {
            throw new RuntimeException("Department already exists");
        }

        Department department = new Department();
        department.setName(departmentDto.getName());
        department.setDescription(departmentDto.getDescription());
        Department saved = departmentRepository.save(department);

        DepartmentDto dto = new DepartmentDto();
        dto.setId(saved.getId());
        dto.setName(saved.getName());
        dto.setDescription(saved.getDescription());
        return dto;
    }

    // Evict both caches when deleting a department
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "departments", allEntries = true),
            @CacheEvict(cacheNames = "employeesByDepartment", allEntries = true)
    })
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found");
        }
        departmentRepository.deleteById(id);
    }
}
