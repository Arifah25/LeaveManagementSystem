package com.lms.LeaveManagementSystem.service.impl;

import com.lms.LeaveManagementSystem.dto.LeaveRequestDto;
import com.lms.LeaveManagementSystem.dto.ManagerDto;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(cacheNames = "managerLeaveRequests", key = "#root.target.getCurrentUserId()")
    @Override
    public List<LeaveRequestDto> getLeaveRequestsForManager() {
        User currentUser = getCurrentUser();
        return leaveRequestRepository.findByStatusAndManager(LeaveStatus.PENDING_MANAGER, currentUser)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(cacheNames = { "managerLeaveRequests", "adminLeaveRequests", "leaveRequest" }, key = "#id")
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
                .timeType(req.getTimeType())
                .leaveType(req.getLeaveType())
                .status(LeaveStatus.PENDING_ADMIN)
                .notes("Approved by manager")
                .build());

        // Evict employee's history cache
        evictEmployeeCache(req.getEmployee().getId());
    }

    @CacheEvict(cacheNames = { "managerLeaveRequests", "leaveRequest" }, key = "#id")
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

        // Evict employee's history cache
        evictEmployeeCache(req.getEmployee().getId());
    }

    // @Cacheable(cacheNames = "adminLeaveRequests")
    @Override
    public List<LeaveRequestDto> getLeaveRequestsForAdmin() {
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING_ADMIN)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(cacheNames = { "adminLeaveRequests", "leaveRequest" }, key = "#id")
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
                .timeType(req.getTimeType())
                .leaveType(req.getLeaveType())
                .status(LeaveStatus.APPROVED)
                .notes("Approved by admin")
                .build());
        // you may also update leave balance here, if not yet done
        updateLeaveBalance(req.getEmployee(), req);

        // Evict employee's caches
        evictEmployeeCache(req.getEmployee().getId());
    }

    @CacheEvict(cacheNames = { "adminLeaveRequests", "leaveRequest" }, key = "#id")
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
                .timeType(req.getTimeType())
                .leaveType(req.getLeaveType())
                .status(LeaveStatus.REJECTED)
                .notes("Rejected by admin")
                .build());

        // Evict employee's caches
        evictEmployeeCache(req.getEmployee().getId());
    }

    @Override
    @Transactional
    public LeaveRequestDto applyLeave(LeaveRequestDto leaveRequestDto) {
        User currentUser = getCurrentUser();
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(currentUser);
        leaveRequest.setStartDate(leaveRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestDto.getEndDate());
        leaveRequest.setTimeType(leaveRequestDto.getTimeType());
        leaveRequest.setLeaveType(leaveRequestDto.getLeaveType());
        leaveRequest.setStatus(LeaveStatus.PENDING_MANAGER);
        leaveRequest.setReason(leaveRequestDto.getReason());
        leaveRequest.setManager(currentUser.getManager());

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        // Evict manager's cache as a new request is available
        evictManagerCache();

        // Evict employee's history cache
        evictEmployeeCache(currentUser.getId());

        return leaveRequestDto;
    }

    @Cacheable(cacheNames = "leaveHistory", key = "#root.target.getCurrentUserId()")
    @Override
    public List<LeaveRequestDto> getLeaveHistoryForEmployee() {
        User currentUser = getCurrentUser();
        return leaveRequestRepository
                .findByEmployee(currentUser)
                .stream()
                .map(history -> {
                    LeaveRequestDto dto = new LeaveRequestDto();
                    dto.setId(history.getId());
                    dto.setEmployeeId(currentUser.getId());
                    dto.setStartDate(history.getStartDate());
                    dto.setEndDate(history.getEndDate());
                    dto.setLeaveType(history.getLeaveType());
                    dto.setTimeType(history.getTimeType());
                    dto.setStatus(history.getStatus());
                    dto.setReason(history.getReason());
                    if (history.getManager() != null) {
                        ManagerDto m = new ManagerDto();
                        m.setId(history.getManager().getId());
                        m.setFullName(history.getManager().getFullName());
                        m.setEmail(history.getManager().getEmail());
                        dto.setManager(m);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "leaveBalance", key = "#root.target.getCurrentUserId()")
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

    @Cacheable(cacheNames = "leaveRequest", key = "#id")
    @Override
    public LeaveRequestDto getLeaveRequestById(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LeaveRequest not found"));
        return mapToDto(leaveRequest);
    }

    @CacheEvict(cacheNames = "leaveRequest", key = "#id")
    @Override
    @Transactional
    public void cancelLeaveRequest(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LeaveRequest not found"));
        User employee = leaveRequest.getEmployee();

        if (leaveRequest.getStatus() == LeaveStatus.PENDING_MANAGER
                || leaveRequest.getStatus() == LeaveStatus.PENDING_ADMIN) {
            leaveRequest.setStatus(LeaveStatus.CANCELLED);
            leaveRequestRepository.save(leaveRequest);

            // Evict appropriate caches
            if (leaveRequest.getStatus() == LeaveStatus.PENDING_MANAGER) {
                evictManagerCache();
            } else if (leaveRequest.getStatus() == LeaveStatus.PENDING_ADMIN) {
                evictAdminCache();
            }

            // Evict employee's history cache
            evictEmployeeCache(employee.getId());
        } else {
            throw new RuntimeException("Only pending leave requests can be cancelled");
        }
    }

    // Helper methods for cache management

    // Get current user ID for cache keys
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    // Evict all caches related to an employee
    @CacheEvict(cacheNames = { "leaveHistory", "leaveBalance" }, key = "#employeeId")
    public void evictEmployeeCache(Long employeeId) {
        // Method intentionally empty - the annotation does the work
    }

    @CacheEvict(cacheNames = "managerLeaveRequests", allEntries = true)
    public void evictManagerCache() {
        // Method intentionally empty - the annotation does the work
    }

    @CacheEvict(cacheNames = "adminLeaveRequests", allEntries = true)
    public void evictAdminCache() {
        // Method intentionally empty - the annotation does the work
    }

    // Helper method to map a LeaveRequest entity to its DTO.
    private LeaveRequestDto mapToDto(LeaveRequest leaveRequest) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(leaveRequest.getId());
        dto.setEmployeeId(leaveRequest.getEmployee().getId());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setTimeType(leaveRequest.getTimeType());
        dto.setLeaveType(leaveRequest.getLeaveType());
        dto.setStatus(leaveRequest.getStatus());
        dto.setReason(leaveRequest.getReason());
        if (leaveRequest.getManager() != null) {
            ManagerDto m = new ManagerDto();
            m.setId(leaveRequest.getManager().getId());
            m.setFullName(leaveRequest.getManager().getFullName());
            m.setEmail(leaveRequest.getManager().getEmail());
            dto.setManager(m);
        }
        return dto;
    }

    // A simple update method for employee leave balance.
    // This should be called after a leave request is approved.
    private void updateLeaveBalance(User employee, LeaveRequest leaveRequest) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployee(employee)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));
        int days = (int) (leaveRequest.getEndDate().toEpochDay() - leaveRequest.getStartDate().toEpochDay() + 1);
        balance.setUsedLeaves(balance.getUsedLeaves() + days);
        leaveBalanceRepository.save(balance);
    }
}
