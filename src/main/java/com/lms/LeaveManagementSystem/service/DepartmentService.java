package com.lms.LeaveManagementSystem.service;

import com.lms.LeaveManagementSystem.dto.DepartmentDto;
import com.lms.LeaveManagementSystem.dto.UserDto;

import java.util.List;

public interface DepartmentService {
    List<DepartmentDto> getAllDepartments();

    List<UserDto> getTotalEmployeeByDepartment(Long id);

    DepartmentDto createDepartment(DepartmentDto departmentDto);

    void deleteDepartment(Long id);

}
