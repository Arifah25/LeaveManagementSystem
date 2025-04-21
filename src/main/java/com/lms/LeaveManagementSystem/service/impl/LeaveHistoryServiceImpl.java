package com.lms.LeaveManagementSystem.service.impl;

import com.lms.LeaveManagementSystem.dto.LeaveHistoryDto;
import com.lms.LeaveManagementSystem.entity.LeaveHistory;
import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.repository.LeaveHistoryRepository;
import com.lms.LeaveManagementSystem.repository.UserRepository;
import com.lms.LeaveManagementSystem.security.MyUserDetails;
import com.lms.LeaveManagementSystem.service.LeaveHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveHistoryServiceImpl implements LeaveHistoryService {

    @Autowired
    private LeaveHistoryRepository leaveHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails details = (MyUserDetails) auth.getPrincipal();
        return details.getUser();
    }

    @Override
    public List<LeaveHistoryDto> getLeaveHistoryForEmployee() {
        User currentUser = getCurrentUser();
        List<LeaveHistory> histories = leaveHistoryRepository.findByEmployee(currentUser);
        return histories.stream().map(history -> {
            LeaveHistoryDto dto = new LeaveHistoryDto();
            dto.setId(history.getId());
            dto.setEmployeeId(history.getEmployee().getId());
            dto.setStartDate(history.getStartDate());
            dto.setEndDate(history.getEndDate());
            dto.setLeaveType(history.getLeaveType().name());
            dto.setStatus(history.getStatus().name());
            dto.setNotes(history.getNotes());
            return dto;
        }).collect(Collectors.toList());
    }
}
