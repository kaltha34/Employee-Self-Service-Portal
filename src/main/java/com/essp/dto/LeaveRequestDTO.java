package com.essp.dto;

import com.essp.model.LeaveRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDTO {
    
    private Long requestId;
    
    private Long employeeId;
    
    private String employeeName;
    
    private String employeeDepartment;
    
    @NotBlank(message = "Leave type is required")
    private String leaveType;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    private String reason;
    
    private LeaveRequest.Status status;
    
    private Long approverId;
    
    private String approverName;
    
    private LocalDate submittedOn;
    
    private MultipartFile document;
    
    private String documentPath;
    
    private String comments;
    
    // Constructor to convert Entity to DTO
    public LeaveRequestDTO(LeaveRequest leaveRequest) {
        this.requestId = leaveRequest.getRequestId();
        this.employeeId = leaveRequest.getEmployee().getEmpId();
        this.employeeName = leaveRequest.getEmployee().getFirstName() + " " + leaveRequest.getEmployee().getLastName();
        this.employeeDepartment = leaveRequest.getEmployee().getDepartment();
        this.leaveType = leaveRequest.getLeaveType();
        this.startDate = leaveRequest.getStartDate();
        this.endDate = leaveRequest.getEndDate();
        this.reason = leaveRequest.getReason();
        this.status = leaveRequest.getStatus();
        if (leaveRequest.getApprover() != null) {
            this.approverId = leaveRequest.getApprover().getEmpId();
            this.approverName = leaveRequest.getApprover().getFirstName() + " " + leaveRequest.getApprover().getLastName();
        }
        this.submittedOn = leaveRequest.getSubmittedOn();
        this.documentPath = leaveRequest.getDocumentPath();
        this.comments = leaveRequest.getComments();
    }
}
