package com.lms.LeaveManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lms.LeaveManagementSystem.enums.Role;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password; // Typically store the encoded password

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    // manager_id is optional, as not all users will have a manager
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = true)
    @JsonIgnore
    private User manager;

    // Optional: Associate user with a department
    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({ "users", "department", "manager" })
    private Department department;

    // Optional: Set of leave requests associated with the user
    @OneToMany(mappedBy = "employee")
    private Set<LeaveRequest> leaveRequests;

    // Optional: Set of leave requests associated with the user as manager
    @OneToMany(mappedBy = "manager")
    private Set<LeaveRequest> managedLeaveRequests;
}
