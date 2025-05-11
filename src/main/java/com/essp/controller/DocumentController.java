package com.essp.controller;

import com.essp.config.FileStorageConfig;
import com.essp.dto.EmployeeDTO;
import com.essp.dto.LeaveRequestDTO;
import com.essp.service.EmployeeService;
import com.essp.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class DocumentController {

    @Autowired
    private FileStorageConfig fileStorageConfig;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/leave/document/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id, HttpServletRequest request) {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        
        // Get leave request
        LeaveRequestDTO leaveRequest = leaveRequestService.getLeaveRequestById(id);
        
        // Check if user is authorized to download the document
        boolean isAdmin = employee.getRole().name().equals("ADMIN");
        boolean isOwner = leaveRequest.getEmployeeId().equals(employee.getEmpId());
        
        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(403).build();
        }
        
        // Check if document exists
        if (leaveRequest.getDocumentPath() == null || leaveRequest.getDocumentPath().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Load document as resource
        Resource resource = fileStorageConfig.loadFileAsResource(leaveRequest.getDocumentPath());
        
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Default content type if type could not be determined
            contentType = "application/octet-stream";
        }
        
        // Fallback to default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
