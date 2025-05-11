package com.essp.controller;

import com.essp.dto.EmployeeDTO;
import com.essp.dto.LeaveRequestDTO;
import com.essp.model.LeaveRequest;
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

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminViewController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/leave-requests")
    public String leaveRequests(@RequestParam(required = false) String status, Model model) {
        List<LeaveRequestDTO> leaveRequests;
        
        if ("all".equals(status)) {
            leaveRequests = leaveRequestService.getAllLeaveRequests();
        } else {
            leaveRequests = leaveRequestService.getPendingLeaveRequests();
        }
        
        model.addAttribute("leaveRequests", leaveRequests);
        return "admin/leave-requests";
    }

    @GetMapping("/leave-requests/view/{id}")
    public String viewLeaveRequest(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            LeaveRequestDTO leaveRequest = leaveRequestService.getLeaveRequestById(id);
            model.addAttribute("leaveRequest", leaveRequest);
            
            // Get employee details
            EmployeeDTO employee = employeeService.getEmployeeById(leaveRequest.getEmployeeId());
            model.addAttribute("employeeDepartment", employee.getDepartment());
            model.addAttribute("employeeEmail", employee.getEmail());
            
            return "admin/view-leave-request";
        } catch (Exception e) {
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
