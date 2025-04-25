package com.lms.LeaveManagementSystem.dto;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaveBalanceDto {
    private Long id;
    private Long employeeId;
    private int totalLeaves;
    private int usedLeaves;

    // getters + setters omitted for brevity

    /** computed at runtime, not round-tripped through cache */
    @JsonIgnore
    public int getRemainingLeaves() {
        return totalLeaves - usedLeaves;
    }
}
