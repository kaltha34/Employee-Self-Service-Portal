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

/**
 * A simplified controller for admin leave management functionality
 */
@Controller
@RequestMapping("/admin/leaves")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLeaveController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private EmployeeService employeeService;

    /**
     * Display all leave requests or filter by status
     */
    @GetMapping
    public String getAllLeaveRequests(@RequestParam(required = false) String status, Model model) {
        try {
            List<LeaveRequestDTO> leaveRequests;
            
            if ("all".equals(status)) {
                leaveRequests = leaveRequestService.getAllLeaveRequests();
                model.addAttribute("currentFilter", "all");
            } else {
                leaveRequests = leaveRequestService.getPendingLeaveRequests();
                model.addAttribute("currentFilter", "pending");
            }
            
            // Add leave requests to the model
            model.addAttribute("leaveRequests", leaveRequests);
            
            return "admin/leaves/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error retrieving leave requests: " + e.getMessage());
            model.addAttribute("leaveRequests", new ArrayList<>());
            return "admin/leaves/list";
        }
    }

    /**
     * Display details of a specific leave request
     */
    @GetMapping("/{id}")
    public String getLeaveRequestDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Retrieve the leave request
            LeaveRequestDTO leaveRequest = leaveRequestService.getLeaveRequestById(id);
            model.addAttribute("leaveRequest", leaveRequest);
            
            // Get employee details
            EmployeeDTO employee = employeeService.getEmployeeById(leaveRequest.getEmployeeId());
            model.addAttribute("employeeEmail", employee.getEmail());
            
            return "admin/leaves/details";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error retrieving leave request: " + e.getMessage());
            return "redirect:/admin/leaves";
        }
    }

    /**
     * Approve a leave request
     */
    @PostMapping("/{id}/approve")
    public String approveLeaveRequest(
            @PathVariable Long id, 
            @RequestParam(required = false) String comments,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Get current admin user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            EmployeeDTO admin = employeeService.getEmployeeByEmail(email);
            
            // Approve the leave request
            leaveRequestService.approveLeaveRequest(id, admin.getEmpId(), comments);
            
            redirectAttributes.addFlashAttribute("successMessage", "Leave request approved successfully");
            return "redirect:/admin/leaves";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving leave request: " + e.getMessage());
            return "redirect:/admin/leaves/" + id;
        }
    }

    /**
     * Reject a leave request
     */
    @PostMapping("/{id}/reject")
    public String rejectLeaveRequest(
            @PathVariable Long id, 
            @RequestParam String comments,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Get current admin user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            EmployeeDTO admin = employeeService.getEmployeeByEmail(email);
            
            // Reject the leave request
            leaveRequestService.rejectLeaveRequest(id, admin.getEmpId(), comments);
            
            redirectAttributes.addFlashAttribute("successMessage", "Leave request rejected successfully");
            return "redirect:/admin/leaves";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting leave request: " + e.getMessage());
            return "redirect:/admin/leaves/" + id;
        }
    }
}
