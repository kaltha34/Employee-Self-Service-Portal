package com.essp.controller;

import com.essp.dto.LeaveBalanceDTO;
import com.essp.service.EmployeeService;
import com.essp.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/leave/balance")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<LeaveBalanceDTO>> getLeaveBalances() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long employeeId = employeeService.getEmployeeByEmail(email).getEmpId();
        
        List<LeaveBalanceDTO> leaveBalances = leaveBalanceService.getLeaveBalancesByEmployee(employeeId);
        return ResponseEntity.ok(leaveBalances);
    }

    @GetMapping("/{leaveType}")
    public ResponseEntity<LeaveBalanceDTO> getLeaveBalanceByType(@PathVariable String leaveType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long employeeId = employeeService.getEmployeeByEmail(email).getEmpId();
        
        LeaveBalanceDTO leaveBalance = leaveBalanceService.getLeaveBalanceByEmployeeAndType(employeeId, leaveType);
        return ResponseEntity.ok(leaveBalance);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveBalanceDTO>> getLeaveBalancesByEmployee(@PathVariable Long employeeId) {
        List<LeaveBalanceDTO> leaveBalances = leaveBalanceService.getLeaveBalancesByEmployee(employeeId);
        return ResponseEntity.ok(leaveBalances);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<LeaveBalanceDTO> createLeaveBalance(@Valid @RequestBody LeaveBalanceDTO leaveBalanceDTO) {
        LeaveBalanceDTO createdLeaveBalance = leaveBalanceService.createLeaveBalance(leaveBalanceDTO);
        return ResponseEntity.ok(createdLeaveBalance);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<LeaveBalanceDTO> updateLeaveBalance(@PathVariable Long id, @Valid @RequestBody LeaveBalanceDTO leaveBalanceDTO) {
        LeaveBalanceDTO updatedLeaveBalance = leaveBalanceService.updateLeaveBalance(id, leaveBalanceDTO);
        return ResponseEntity.ok(updatedLeaveBalance);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reset-annual")
    public ResponseEntity<Void> resetAnnualLeaveBalances(@RequestParam int defaultBalance) {
        leaveBalanceService.resetAnnualLeaveBalances(defaultBalance);
        return ResponseEntity.ok().build();
    }
}
