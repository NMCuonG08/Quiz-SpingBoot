package com.example.Repository;

import com.example.Entity.LeaveRequest;

import com.example.Enum.Leave_Status_Enum;
import com.example.Enum.Leave_Type_Enum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {
    
    // Find leave requests by employee
    List<LeaveRequest> findByEmployeeId(UUID employeeId);
    
    // Find leave requests by status
    List<LeaveRequest> findByStatus(Leave_Status_Enum status);
    
    // Find leave requests by leave type
    List<LeaveRequest> findByLeaveType(Leave_Type_Enum leaveType);
    
    // Find pending leave requests
    List<LeaveRequest> findByStatusOrderByCreatedAtAsc(Leave_Status_Enum status);
    
    // Find leave requests by date range
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.startDate >= :startDate AND lr.endDate <= :endDate")
    List<LeaveRequest> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find overlapping leave requests for an employee
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
           "AND lr.status = 'APPROVED' " +
           "AND ((lr.startDate <= :endDate AND lr.endDate >= :startDate))")
    List<LeaveRequest> findOverlappingLeaveRequests(@Param("employeeId") UUID employeeId, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    // Find leave requests approved by a specific manager
//    List<LeaveRequest> findByApprovedById(UUID approverId);
    
    // Count pending leave requests for department
    @Query("SELECT COUNT(lr) FROM LeaveRequest lr WHERE lr.employee.department.id = :departmentId AND lr.status = 'PENDING'")
    Long countPendingLeaveRequestsByDepartment(@Param("departmentId") UUID departmentId);
    
    // Find leave requests for current month
    @Query("SELECT lr FROM LeaveRequest lr WHERE YEAR(lr.startDate) = YEAR(CURRENT_DATE) AND MONTH(lr.startDate) = MONTH(CURRENT_DATE)")
    List<LeaveRequest> findCurrentMonthLeaveRequests();
}
