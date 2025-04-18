package com.lms.LeaveManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // One-to-Many relationship with users
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private Set<User> users;
}
