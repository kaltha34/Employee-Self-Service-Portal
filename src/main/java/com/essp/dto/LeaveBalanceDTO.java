package com.essp.dto;

import com.essp.model.LeaveBalance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDTO {
    
    private Long id;
    
    private Long employeeId;
    
    private String leaveType;
    
    private Integer balance;
    
    // Constructor to convert Entity to DTO
    public LeaveBalanceDTO(LeaveBalance leaveBalance) {
        this.id = leaveBalance.getId();
        this.employeeId = leaveBalance.getEmployee().getEmpId();
        this.leaveType = leaveBalance.getLeaveType();
        this.balance = leaveBalance.getBalance();
    }
}
