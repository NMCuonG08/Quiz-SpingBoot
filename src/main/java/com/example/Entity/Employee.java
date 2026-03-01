package com.example.Entity;

import com.example.Enum.Role_Enum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

import java.util.ArrayList;
import java.util.List;

@Table(name = "employee")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Employee extends BaseEntity {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role_Enum role = Role_Enum.EMPLOYEE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    @JsonIgnore
    private Department department;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LeaveRequest> leaveRequests = new ArrayList<>();

    // One-to-Many relationship with Leave Requests (as approver)
    // @OneToMany(mappedBy = "approvedBy", cascade = CascadeType.ALL, fetch =
    // FetchType.LAZY)
    // @JsonIgnore
    // private List<LeaveRequest> approvedLeaveRequests = new ArrayList<>();

    @OneToOne(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Approve approve;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    @JsonIgnore
    private Test test;

    // Utility methods for managing relationships
    public void setDepartment(Department department) {
        this.department = department;
        if (department != null && !department.getEmployees().contains(this)) {
            department.getEmployees().add(this);
        }
    }

    public void addLeaveRequest(LeaveRequest leaveRequest) {
        if (leaveRequests == null) {
            leaveRequests = new ArrayList<>();
        }
        leaveRequests.add(leaveRequest);
        leaveRequest.setEmployee(this);
    }

    public void removeLeaveRequest(LeaveRequest leaveRequest) {
        if (leaveRequests != null) {
            leaveRequests.remove(leaveRequest);
            leaveRequest.setEmployee(null);
        }
    }

    public void softDelete() {
        super.delete();
    }
}
