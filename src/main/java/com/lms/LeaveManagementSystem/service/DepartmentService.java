package com.lms.LeaveManagementSystem.service;

import com.lms.LeaveManagementSystem.dto.DepartmentDto;
import java.util.List;

public interface DepartmentService {
    List<DepartmentDto> getAllDepartments();
}
