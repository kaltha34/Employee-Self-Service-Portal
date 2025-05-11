package com.essp.repository;

import com.essp.model.Employee;
import com.essp.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    
    List<LeaveRequest> findByEmployeeOrderBySubmittedOnDesc(Employee employee);
    
    List<LeaveRequest> findByStatusOrderBySubmittedOnDesc(LeaveRequest.Status status);
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = ?1 AND lr.employee.department = ?2")
    List<LeaveRequest> findByStatusAndDepartment(LeaveRequest.Status status, String department);
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.startDate >= ?1 AND lr.endDate <= ?2")
    List<LeaveRequest> findByDateRange(LocalDate startDate, LocalDate endDate);
}
