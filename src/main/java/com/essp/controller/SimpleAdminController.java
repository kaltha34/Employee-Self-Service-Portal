package com.essp.controller;

import com.essp.dto.LeaveRequestDTO;
import com.essp.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

/**
 * A simplified controller for admin functionality
 */
@Controller
@RequestMapping("/admin-simple")
@PreAuthorize("hasRole('ADMIN')")
public class SimpleAdminController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    /**
     * Show all leave requests
     */
    @GetMapping("/requests")
    public String showAllRequests(Model model) {
        try {
            List<LeaveRequestDTO> requests = leaveRequestService.getAllLeaveRequests();
            model.addAttribute("requests", requests);
            return "admin/simple/all-requests";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading leave requests: " + e.getMessage());
            model.addAttribute("requests", Collections.emptyList());
            return "admin/simple/all-requests";
        }
    }

    /**
     * Show pending leave requests
     */
    @GetMapping("/pending")
    public String showPendingRequests(Model model) {
        try {
            List<LeaveRequestDTO> requests = leaveRequestService.getPendingLeaveRequests();
            model.addAttribute("requests", requests);
            return "admin/simple/pending-requests";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading pending requests: " + e.getMessage());
            model.addAttribute("requests", Collections.emptyList());
            return "admin/simple/pending-requests";
        }
    }

    /**
     * Approve a leave request
     */
    @PostMapping("/approve/{id}")
    public String approveRequest(@PathVariable Long id, @RequestParam(required = false) String comments, 
                                RedirectAttributes redirectAttributes) {
        try {
            // Get current user as approver
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            // Hard-coded admin ID for now - in a real app, you'd look this up
            Long adminId = 1L;
            
            // Approve the request
            leaveRequestService.approveLeaveRequest(id, adminId, comments);
            
            redirectAttributes.addFlashAttribute("successMessage", "Leave request approved successfully");
            return "redirect:/admin-simple/requests";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving request: " + e.getMessage());
            return "redirect:/admin-simple/requests";
        }
    }

    /**
     * Reject a leave request
     */
    @PostMapping("/reject/{id}")
    public String rejectRequest(@PathVariable Long id, @RequestParam String comments, 
                               RedirectAttributes redirectAttributes) {
        try {
            // Get current user as approver
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            // Hard-coded admin ID for now - in a real app, you'd look this up
            Long adminId = 1L;
            
            // Reject the request
            leaveRequestService.rejectLeaveRequest(id, adminId, comments);
            
            redirectAttributes.addFlashAttribute("successMessage", "Leave request rejected successfully");
            return "redirect:/admin-simple/requests";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting request: " + e.getMessage());
            return "redirect:/admin-simple/requests";
        }
    }
}
