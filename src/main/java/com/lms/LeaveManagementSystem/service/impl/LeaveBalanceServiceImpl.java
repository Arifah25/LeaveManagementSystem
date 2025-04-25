package com.lms.LeaveManagementSystem.service.impl;

import com.lms.LeaveManagementSystem.dto.LeaveBalanceDto;
import com.lms.LeaveManagementSystem.entity.LeaveBalance;
import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.repository.LeaveBalanceRepository;
import com.lms.LeaveManagementSystem.repository.UserRepository;
import com.lms.LeaveManagementSystem.security.MyUserDetails;
import com.lms.LeaveManagementSystem.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails details = (MyUserDetails) auth.getPrincipal();
        return details.getUser();
    }

    /**
     * Cache the leave balance for the current user by their ID.
     */
    @Override
    @Cacheable(cacheNames = "leaveBalance", key = "#root.target.getCurrentUser().id")
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

    /**
     * Evict the cached leave balance when the balance changes (e.g., after applying
     * or approving leave).
     * This should be called from your LeaveServiceImpl where balance is modified.
     */
    @CacheEvict(cacheNames = "leaveBalance", key = "#root.target.getCurrentUser().id")
    public void evictLeaveBalanceCache() {
        // Intentionally empty - used only for cache eviction
    }
}
