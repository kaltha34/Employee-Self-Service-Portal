package com.essp.repository;

import com.essp.model.Employee;
import com.essp.model.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    
    List<LeaveBalance> findByEmployee(Employee employee);
    
    Optional<LeaveBalance> findByEmployeeAndLeaveType(Employee employee, String leaveType);
}
