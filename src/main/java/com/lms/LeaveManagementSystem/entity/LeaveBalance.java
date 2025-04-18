package com.lms.LeaveManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_balances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User employee;

    @Column(nullable = false)
    private int totalLeaves;

    @Column(nullable = false)
    private int usedLeaves;

    public int getRemainingLeaves() {
        return totalLeaves - usedLeaves;
    }
}
