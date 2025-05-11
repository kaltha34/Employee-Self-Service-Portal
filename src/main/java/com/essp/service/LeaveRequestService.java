package com.essp.service;

import com.essp.dto.LeaveRequestDTO;
import com.essp.exception.ResourceNotFoundException;
import com.essp.model.Employee;
import com.essp.model.LeaveBalance;
import com.essp.model.LeaveRequest;
import com.essp.repository.EmployeeRepository;
import com.essp.repository.LeaveBalanceRepository;
import com.essp.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<LeaveRequestDTO> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(LeaveRequestDTO::new)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestDTO> getLeaveRequestsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        return leaveRequestRepository.findByEmployeeOrderBySubmittedOnDesc(employee).stream()
                .map(LeaveRequestDTO::new)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestDTO> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatusOrderBySubmittedOnDesc(LeaveRequest.Status.PENDING).stream()
                .map(LeaveRequestDTO::new)
                .collect(Collectors.toList());
    }

    public LeaveRequestDTO getLeaveRequestById(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        return new LeaveRequestDTO(leaveRequest);
    }

    @Transactional
    public LeaveRequestDTO createLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        Employee employee = employeeRepository.findById(leaveRequestDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + leaveRequestDTO.getEmployeeId()));
        
        // Validate leave balance
        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveRequestDTO.getLeaveType())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for type: " + leaveRequestDTO.getLeaveType()));
        
        long leaveDays = ChronoUnit.DAYS.between(leaveRequestDTO.getStartDate(), leaveRequestDTO.getEndDate()) + 1;
        
        if (leaveBalance.getBalance() < leaveDays) {
            throw new IllegalArgumentException("Insufficient leave balance. Available: " + leaveBalance.getBalance() + ", Requested: " + leaveDays);
        }
        
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveRequestDTO.getLeaveType());
        leaveRequest.setStartDate(leaveRequestDTO.getStartDate());
        leaveRequest.setEndDate(leaveRequestDTO.getEndDate());
        leaveRequest.setReason(leaveRequestDTO.getReason());
        leaveRequest.setStatus(LeaveRequest.Status.PENDING);
        leaveRequest.setSubmittedOn(LocalDate.now());
        
        // Handle document upload if present
        if (leaveRequestDTO.getDocument() != null && !leaveRequestDTO.getDocument().isEmpty()) {
            String documentPath = saveDocument(leaveRequestDTO.getDocument());
            leaveRequest.setDocumentPath(documentPath);
        }
        
        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        return new LeaveRequestDTO(savedLeaveRequest);
    }

    @Transactional
    public LeaveRequestDTO approveLeaveRequest(Long id, Long approverId, String comments) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));
        
        if (leaveRequest.getStatus() != LeaveRequest.Status.PENDING) {
            throw new IllegalArgumentException("Leave request is already " + leaveRequest.getStatus());
        }
        
        leaveRequest.setStatus(LeaveRequest.Status.APPROVED);
        leaveRequest.setApprover(approver);
        leaveRequest.setComments(comments);
        
        // Deduct from leave balance
        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeAndLeaveType(leaveRequest.getEmployee(), leaveRequest.getLeaveType())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));
        
        long leaveDays = ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
        leaveBalance.setBalance(leaveBalance.getBalance() - (int) leaveDays);
        leaveBalanceRepository.save(leaveBalance);
        
        LeaveRequest updatedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        return new LeaveRequestDTO(updatedLeaveRequest);
    }

    @Transactional
    public LeaveRequestDTO rejectLeaveRequest(Long id, Long approverId, String comments) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));
        
        if (leaveRequest.getStatus() != LeaveRequest.Status.PENDING) {
            throw new IllegalArgumentException("Leave request is already " + leaveRequest.getStatus());
        }
        
        leaveRequest.setStatus(LeaveRequest.Status.REJECTED);
        leaveRequest.setApprover(approver);
        leaveRequest.setComments(comments);
        
        LeaveRequest updatedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        return new LeaveRequestDTO(updatedLeaveRequest);
    }

    private String saveDocument(MultipartFile file) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            
            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
