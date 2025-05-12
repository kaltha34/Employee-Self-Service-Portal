package com.essp.controller;

import com.essp.dto.EmployeeDTO;
import com.essp.dto.LeaveRequestDTO;
import com.essp.service.EmployeeService;
import com.essp.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminViewController {
    
    // Simple dashboard method that doesn't rely on complex data structures
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            List<LeaveRequestDTO> pendingRequests = leaveRequestService.getPendingLeaveRequests();
            model.addAttribute("pendingRequests", pendingRequests);
            return "admin/dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            model.addAttribute("pendingRequests", new ArrayList<LeaveRequestDTO>());
            return "admin/dashboard";
        }
    }

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/leave-requests")
    public String leaveRequests(@RequestParam(required = false) String status, Model model) {
        try {
            List<LeaveRequestDTO> leaveRequests;
            
            if ("all".equals(status)) {
                leaveRequests = leaveRequestService.getAllLeaveRequests();
            } else {
                leaveRequests = leaveRequestService.getPendingLeaveRequests();
            }
            
            model.addAttribute("leaveRequests", leaveRequests);
            return "admin/leave-requests";
        } catch (Exception e) {
            // Log the error
            System.err.println("Error retrieving leave requests: " + e.getMessage());
            e.printStackTrace();
            
            // Add error message to the model
            model.addAttribute("errorMessage", "Failed to retrieve leave requests: " + e.getMessage());
            model.addAttribute("leaveRequests", new ArrayList<LeaveRequestDTO>());
            return "admin/leave-requests";
        }
    }

    @GetMapping("/leave-requests/view/{id}")
    public String viewLeaveRequest(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Get the leave request by ID
            LeaveRequestDTO leaveRequest = leaveRequestService.getLeaveRequestById(id);
            
            // Add the leave request to the model
            model.addAttribute("leaveRequest", leaveRequest);
            
            // Return the view template
            return "admin/view-leave-request";
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error viewing leave request with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            
            // Add error message to redirect
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to load leave request: " + e.getMessage());
            return "redirect:/admin/leave-requests";
        }
    }

    @PostMapping("/leave-requests/approve/{id}")
    public String approveLeaveRequest(@PathVariable Long id, @RequestParam(required = false) String comments, 
                                    RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO admin = employeeService.getEmployeeByEmail(email);
        
        try {
            leaveRequestService.approveLeaveRequest(id, admin.getEmpId(), comments);
            redirectAttributes.addFlashAttribute("successMessage", "Leave request approved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve leave request: " + e.getMessage());
        }
        
        return "redirect:/admin/leave-requests";
    }

    @PostMapping("/leave-requests/reject/{id}")
    public String rejectLeaveRequest(@PathVariable Long id, @RequestParam String comments, 
                                   RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO admin = employeeService.getEmployeeByEmail(email);
        
        try {
            leaveRequestService.rejectLeaveRequest(id, admin.getEmpId(), comments);
            redirectAttributes.addFlashAttribute("successMessage", "Leave request rejected successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reject leave request: " + e.getMessage());
        }
        
        return "redirect:/admin/leave-requests";
    }
}
