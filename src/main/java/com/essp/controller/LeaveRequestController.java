package com.essp.controller;

import com.essp.dto.LeaveRequestDTO;
import com.essp.service.EmployeeService;
import com.essp.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/history")
    public ResponseEntity<List<LeaveRequestDTO>> getLeaveHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long employeeId = employeeService.getEmployeeByEmail(email).getEmpId();
        
        List<LeaveRequestDTO> leaveHistory = leaveRequestService.getLeaveRequestsByEmployee(employeeId);
        return ResponseEntity.ok(leaveHistory);
    }

    @PostMapping("/apply")
    public ResponseEntity<LeaveRequestDTO> applyForLeave(@Valid @RequestBody LeaveRequestDTO leaveRequestDTO,
                                                        @RequestParam(required = false) MultipartFile document) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long employeeId = employeeService.getEmployeeByEmail(email).getEmpId();
        
        leaveRequestDTO.setEmployeeId(employeeId);
        
        if (document != null && !document.isEmpty()) {
            leaveRequestDTO.setDocument(document);
        }
        
        LeaveRequestDTO createdLeaveRequest = leaveRequestService.createLeaveRequest(leaveRequestDTO);
        return ResponseEntity.ok(createdLeaveRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getLeaveRequestById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long employeeId = employeeService.getEmployeeByEmail(email).getEmpId();
        
        LeaveRequestDTO leaveRequestDTO = leaveRequestService.getLeaveRequestById(id);
        
        // Check if the leave request belongs to the authenticated employee
        if (!leaveRequestDTO.getEmployeeId().equals(employeeId)) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(leaveRequestDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/pending")
    public ResponseEntity<List<LeaveRequestDTO>> getPendingLeaveRequests() {
        List<LeaveRequestDTO> pendingLeaveRequests = leaveRequestService.getPendingLeaveRequests();
        return ResponseEntity.ok(pendingLeaveRequests);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/approve/{id}")
    public ResponseEntity<LeaveRequestDTO> approveLeaveRequest(@PathVariable Long id, @RequestParam String comments) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long approverId = employeeService.getEmployeeByEmail(email).getEmpId();
        
        LeaveRequestDTO approvedLeaveRequest = leaveRequestService.approveLeaveRequest(id, approverId, comments);
        return ResponseEntity.ok(approvedLeaveRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reject/{id}")
    public ResponseEntity<LeaveRequestDTO> rejectLeaveRequest(@PathVariable Long id, @RequestParam String comments) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long approverId = employeeService.getEmployeeByEmail(email).getEmpId();
        
        LeaveRequestDTO rejectedLeaveRequest = leaveRequestService.rejectLeaveRequest(id, approverId, comments);
        return ResponseEntity.ok(rejectedLeaveRequest);
    }
}
