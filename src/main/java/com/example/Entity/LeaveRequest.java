package com.example.Entity;

import com.example.Enum.Leave_Status_Enum;
import com.example.Enum.Leave_Type_Enum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

@Table(name="leave_requests")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LeaveRequest extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false)
    private Leave_Type_Enum leaveType;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Leave_Status_Enum status = Leave_Status_Enum.PENDING;

    @Column(name = "approval_comments", length = 500)
    private String approvalComments;

    // Many-to-One relationship with Employee (requester)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Employee employee;

    // Many-to-One relationship with Employee (approver)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "approved_by_id", referencedColumnName = "id")
//    @JsonIgnore
//    private Employee approvedBy;


    @OneToOne( mappedBy = "leaveRequest",fetch = FetchType.LAZY)
    @JsonIgnore
    private Approve approve;

    // Utility methods for managing relationships
    public void setEmployee(Employee employee) {
        this.employee = employee;
        if (employee != null && !employee.getLeaveRequests().contains(this)) {
            employee.getLeaveRequests().add(this);
        }
    }

//    public void setApprovedBy(Employee approver) {
//        this.approvedBy = approver;
//        if (approver != null && !approver.getApprovedLeaveRequests().contains(this)) {
//            approver.getApprovedLeaveRequests().add(this);
//        }
//    }

    // Business methods
    public boolean isPending() {
        return status == Leave_Status_Enum.PENDING;
    }

    public boolean isApproved() {
        return status == Leave_Status_Enum.APPROVED;
    }

    public boolean isRejected() {
        return status == Leave_Status_Enum.REJECTED;
    }

    public boolean isCancelled() {
        return status == Leave_Status_Enum.CANCELLED;
    }

    public int getDurationInDays() {
        if (startDate != null && endDate != null) {
            return (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
        }
        return 0;
    }

    public boolean isOverlapping(LocalDate start, LocalDate end) {
        return !(endDate.isBefore(start) || startDate.isAfter(end));
    }
}
