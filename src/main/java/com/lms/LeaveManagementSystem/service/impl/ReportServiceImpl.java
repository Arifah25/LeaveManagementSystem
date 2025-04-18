package com.lms.LeaveManagementSystem.service.impl;

import com.lms.LeaveManagementSystem.dto.ReportDto;
import com.lms.LeaveManagementSystem.enums.LeaveStatus;
import com.lms.LeaveManagementSystem.repository.LeaveRequestRepository;
import com.lms.LeaveManagementSystem.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Override
    public ReportDto getManagerReport() {
        ReportDto report = new ReportDto();
        List<?> all = leaveRequestRepository.findAll();
        report.setTotalRequests(all.size());
        List<?> approved = leaveRequestRepository.findByStatus(LeaveStatus.APPROVED);
        report.setApprovedRequests(approved.size());
        List<?> rejected = leaveRequestRepository.findByStatus(LeaveStatus.REJECTED);
        report.setRejectedRequests(rejected.size());
        List<?> pending = leaveRequestRepository
                .findByStatusIn(List.of(LeaveStatus.PENDING_MANAGER, LeaveStatus.PENDING_ADMIN));
        report.setPendingRequests(pending.size());
        return report;
    }
}
