package com.lms.LeaveManagementSystem.service.impl;

import com.lms.LeaveManagementSystem.dto.LeaveRequestDto;
import com.lms.LeaveManagementSystem.dto.LeaveHistoryDto;
import com.lms.LeaveManagementSystem.dto.LeaveBalanceDto;
import com.lms.LeaveManagementSystem.enums.LeaveStatus;
import com.lms.LeaveManagementSystem.enums.LeaveType;
import com.lms.LeaveManagementSystem.entity.LeaveRequest;
import com.lms.LeaveManagementSystem.entity.LeaveHistory;
import com.lms.LeaveManagementSystem.entity.LeaveBalance;
import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.repository.LeaveRequestRepository;
import com.lms.LeaveManagementSystem.repository.LeaveHistoryRepository;
import com.lms.LeaveManagementSystem.repository.LeaveBalanceRepository;
import com.lms.LeaveManagementSystem.repository.UserRepository;
import com.lms.LeaveManagementSystem.security.MyUserDetails;
import com.lms.LeaveManagementSystem.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveHistoryRepository leaveHistoryRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails details = (MyUserDetails) auth.getPrincipal();
        return details.getUser();
    }

    @Override
    public List<LeaveRequestDto> getLeaveRequestsForManager() {
        User mgr = getCurrentUser();
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING_MANAGER)
                .stream()
                // only requests in this manager’s department
                .filter(req -> req.getEmployee().getDepartment()
                        .equals(mgr.getDepartment()))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void managerApproveLeave(Long id) {
        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        req.setStatus(LeaveStatus.PENDING_ADMIN);
        leaveRequestRepository.save(req);
        // record history
        leaveHistoryRepository.save(LeaveHistory.builder()
                .employee(req.getEmployee())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .leaveType(req.getLeaveType())
                .status(LeaveStatus.PENDING_ADMIN)
                .notes("Approved by manager")
                .build());
    }

    @Override
    @Transactional
    public void managerRejectLeave(Long id) {
        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        req.setStatus(LeaveStatus.REJECTED);
        leaveRequestRepository.save(req);
        leaveHistoryRepository.save(LeaveHistory.builder()
                .employee(req.getEmployee())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .leaveType(req.getLeaveType())
                .status(LeaveStatus.REJECTED)
                .notes("Rejected by manager")
                .build());
    }

    @Override
    public List<LeaveRequestDto> getLeaveRequestsForAdmin() {
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING_ADMIN)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void adminApproveLeave(Long id) {
        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        req.setStatus(LeaveStatus.APPROVED);
        leaveRequestRepository.save(req);
        leaveHistoryRepository.save(LeaveHistory.builder()
                .employee(req.getEmployee())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .leaveType(req.getLeaveType())
                .status(LeaveStatus.APPROVED)
                .notes("Approved by admin")
                .build());
        // you may also update leave balance here, if not yet done
    }

    @Override
    @Transactional
    public void adminRejectLeave(Long id) {
        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        req.setStatus(LeaveStatus.REJECTED);
        leaveRequestRepository.save(req);
        leaveHistoryRepository.save(LeaveHistory.builder()
                .employee(req.getEmployee())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .leaveType(req.getLeaveType())
                .status(LeaveStatus.REJECTED)
                .notes("Rejected by admin")
                .build());
    }

    @Override
    @Transactional
    public LeaveRequestDto applyLeave(LeaveRequestDto leaveRequestDto) {
        User currentUser = getCurrentUser();
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(currentUser);
        leaveRequest.setStartDate(leaveRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestDto.getEndDate());
        leaveRequest.setStartTime(leaveRequestDto.getStartTime());
        leaveRequest.setEndTime(leaveRequestDto.getEndTime());
        leaveRequest.setLeaveType(LeaveType.valueOf(leaveRequestDto.getLeaveType().toUpperCase()));
        leaveRequest.setStatus(LeaveStatus.PENDING_MANAGER);
        leaveRequest.setReason(leaveRequestDto.getReason());

        leaveRequest = leaveRequestRepository.save(leaveRequest);
        leaveRequestDto.setId(leaveRequest.getId());
        leaveRequestDto.setStatus(leaveRequest.getStatus().name());
        leaveRequestDto.setEmployeeId(currentUser.getId());
        return leaveRequestDto;
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

    @Override
    public LeaveBalanceDto getLeaveBalanceForEmployee() {
        User currentUser = getCurrentUser();
        LeaveBalance balance = leaveBalanceRepository.findByEmployee(currentUser)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));
        LeaveBalanceDto dto = new LeaveBalanceDto();
        dto.setId(balance.getId());
        dto.setEmployeeId(currentUser.getId());
        dto.setTotalLeaves(balance.getTotalLeaves());
        dto.setUsedLeaves(balance.getUsedLeaves());
        return dto;
    }

    @Override
    public LeaveRequestDto getLeaveRequestById(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LeaveRequest not found"));
        return mapToDto(leaveRequest);
    }

    @Override
    @Transactional
    public void cancelLeaveRequest(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LeaveRequest not found"));
        if (leaveRequest.getStatus() == LeaveStatus.PENDING_MANAGER
                || leaveRequest.getStatus() == LeaveStatus.PENDING_ADMIN) {
            leaveRequest.setStatus(LeaveStatus.CANCELLED);
            leaveRequestRepository.save(leaveRequest);
        } else {
            throw new RuntimeException("Only pending leave requests can be cancelled");
        }
    }

    // Helper method to map a LeaveRequest entity to its DTO.
    private LeaveRequestDto mapToDto(LeaveRequest leaveRequest) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(leaveRequest.getId());
        dto.setEmployeeId(leaveRequest.getEmployee().getId());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setLeaveType(leaveRequest.getLeaveType().name());
        dto.setStatus(leaveRequest.getStatus().name());
        dto.setReason(leaveRequest.getReason());
        return dto;
    }

    // A simple update method for employee leave balance.
    private void updateLeaveBalance(User employee, LeaveRequest leaveRequest) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployee(employee)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));
        int days = (int) (leaveRequest.getEndDate().toEpochDay() - leaveRequest.getStartDate().toEpochDay() + 1);
        balance.setUsedLeaves(balance.getUsedLeaves() + days);
        leaveBalanceRepository.save(balance);
    }
}
