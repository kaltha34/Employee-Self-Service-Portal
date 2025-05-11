package com.essp.service;

import com.essp.dto.LeaveBalanceDTO;
import com.essp.exception.ResourceNotFoundException;
import com.essp.model.Employee;
import com.essp.model.LeaveBalance;
import com.essp.repository.EmployeeRepository;
import com.essp.repository.LeaveBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<LeaveBalanceDTO> getLeaveBalancesByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        return leaveBalanceRepository.findByEmployee(employee).stream()
                .map(LeaveBalanceDTO::new)
                .collect(Collectors.toList());
    }

    public LeaveBalanceDTO getLeaveBalanceByEmployeeAndType(Long employeeId, String leaveType) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for type: " + leaveType));
        
        return new LeaveBalanceDTO(leaveBalance);
    }

    @Transactional
    public LeaveBalanceDTO createLeaveBalance(LeaveBalanceDTO leaveBalanceDTO) {
        Employee employee = employeeRepository.findById(leaveBalanceDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + leaveBalanceDTO.getEmployeeId()));
        
        // Check if leave balance already exists
        if (leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveBalanceDTO.getLeaveType()).isPresent()) {
            throw new IllegalArgumentException("Leave balance already exists for this type");
        }
        
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setEmployee(employee);
        leaveBalance.setLeaveType(leaveBalanceDTO.getLeaveType());
        leaveBalance.setBalance(leaveBalanceDTO.getBalance());
        
        LeaveBalance savedLeaveBalance = leaveBalanceRepository.save(leaveBalance);
        return new LeaveBalanceDTO(savedLeaveBalance);
    }

    @Transactional
    public LeaveBalanceDTO updateLeaveBalance(Long id, LeaveBalanceDTO leaveBalanceDTO) {
        LeaveBalance leaveBalance = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found with id: " + id));
        
        leaveBalance.setBalance(leaveBalanceDTO.getBalance());
        
        LeaveBalance updatedLeaveBalance = leaveBalanceRepository.save(leaveBalance);
        return new LeaveBalanceDTO(updatedLeaveBalance);
    }

    @Transactional
    public void resetAnnualLeaveBalances(int defaultBalance) {
        List<LeaveBalance> annualLeaveBalances = leaveBalanceRepository.findAll().stream()
                .filter(lb -> "Annual".equals(lb.getLeaveType()))
                .collect(Collectors.toList());
        
        for (LeaveBalance leaveBalance : annualLeaveBalances) {
            leaveBalance.setBalance(defaultBalance);
            leaveBalanceRepository.save(leaveBalance);
        }
    }
}
