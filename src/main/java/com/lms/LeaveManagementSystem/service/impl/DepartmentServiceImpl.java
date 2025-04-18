package com.lms.LeaveManagementSystem.service.impl;

import com.lms.LeaveManagementSystem.dto.DepartmentDto;
import com.lms.LeaveManagementSystem.entity.Department;
import com.lms.LeaveManagementSystem.repository.DepartmentRepository;
import com.lms.LeaveManagementSystem.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

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
}