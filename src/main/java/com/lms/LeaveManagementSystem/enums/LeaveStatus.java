package com.lms.LeaveManagementSystem.enums;

public enum LeaveStatus {
    PENDING_MANAGER, // awaiting department manager
    PENDING_ADMIN, // awaiting admin after manager ok
    APPROVED, // fully approved
    REJECTED,
    CANCELLED
}

// PENDING,
// APPROVED,
// REJECTED,
// CANCELLED