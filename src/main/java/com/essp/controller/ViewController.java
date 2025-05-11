package com.essp.controller;

import com.essp.dto.EmployeeDTO;
import com.essp.dto.LeaveBalanceDTO;
import com.essp.dto.LeaveRequestDTO;
import com.essp.model.LeaveRequest;
import com.essp.service.EmployeeService;
import com.essp.service.LeaveBalanceService;
import com.essp.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        // If user is already logged in, redirect to dashboard
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get current employee
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        model.addAttribute("employee", employee);

        // Get leave balances
        List<LeaveBalanceDTO> leaveBalances = leaveBalanceService.getLeaveBalancesByEmployee(employee.getEmpId());
        model.addAttribute("leaveBalances", leaveBalances);

        // Get recent leave requests
        List<LeaveRequestDTO> leaveRequests = leaveRequestService.getLeaveRequestsByEmployee(employee.getEmpId());
        List<LeaveRequestDTO> recentLeaveRequests = leaveRequests.stream()
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentLeaveRequests", recentLeaveRequests);

        // For admin, get pending leave requests
        if (employee.getRole().name().equals("ADMIN")) {
            List<LeaveRequestDTO> pendingLeaveRequests = leaveRequestService.getPendingLeaveRequests();
            model.addAttribute("pendingLeaveRequests", pendingLeaveRequests);
        }

        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        model.addAttribute("employee", employee);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute EmployeeDTO employeeDTO, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO currentEmployee = employeeService.getEmployeeByEmail(email);
        
        // Update only allowed fields
        employeeDTO.setEmpId(currentEmployee.getEmpId());
        employeeDTO.setEmail(currentEmployee.getEmail());
        employeeDTO.setRole(currentEmployee.getRole());
        employeeDTO.setDepartment(currentEmployee.getDepartment());
        
        try {
            employeeService.updateEmployee(currentEmployee.getEmpId(), employeeDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }

    @GetMapping("/leave/apply")
    public String applyLeave(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        
        // Get leave balances
        List<LeaveBalanceDTO> leaveBalances = leaveBalanceService.getLeaveBalancesByEmployee(employee.getEmpId());
        model.addAttribute("leaveBalances", leaveBalances);
        
        // Create new leave request
        LeaveRequestDTO leaveRequestDTO = new LeaveRequestDTO();
        leaveRequestDTO.setStartDate(LocalDate.now());
        leaveRequestDTO.setEndDate(LocalDate.now());
        model.addAttribute("leaveRequest", leaveRequestDTO);
        
        return "leave/apply";
    }

    @PostMapping("/leave/apply")
    public String submitLeaveRequest(@ModelAttribute LeaveRequestDTO leaveRequestDTO, 
                                    @ModelAttribute("document") MultipartFile document,
                                    RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        
        leaveRequestDTO.setEmployeeId(employee.getEmpId());
        
        if (document != null && !document.isEmpty()) {
            leaveRequestDTO.setDocument(document);
        }
        
        try {
            leaveRequestService.createLeaveRequest(leaveRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Leave request submitted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to submit leave request: " + e.getMessage());
            return "redirect:/leave/apply";
        }
        
        return "redirect:/leave/history";
    }

    @GetMapping("/leave/history")
    public String leaveHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        
        List<LeaveRequestDTO> leaveRequests = leaveRequestService.getLeaveRequestsByEmployee(employee.getEmpId());
        model.addAttribute("leaveRequests", leaveRequests);
        
        return "leave/history";
    }

    @GetMapping("/leave/view/{id}")
    public String viewLeaveRequest(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        
        try {
            LeaveRequestDTO leaveRequest = leaveRequestService.getLeaveRequestById(id);
            
            // Check if the leave request belongs to the authenticated employee
            if (!leaveRequest.getEmployeeId().equals(employee.getEmpId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to view this leave request");
                return "redirect:/leave/history";
            }
            
            model.addAttribute("leaveRequest", leaveRequest);
            return "leave/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to load leave request: " + e.getMessage());
            return "redirect:/leave/history";
        }
    }

    @GetMapping("/leave/balance")
    public String leaveBalance(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        
        List<LeaveBalanceDTO> leaveBalances = leaveBalanceService.getLeaveBalancesByEmployee(employee.getEmpId());
        model.addAttribute("leaveBalances", leaveBalances);
        
        // Calculate used leaves
        List<LeaveRequestDTO> approvedLeaveRequests = leaveRequestService.getLeaveRequestsByEmployee(employee.getEmpId()).stream()
                .filter(request -> request.getStatus() == LeaveRequest.Status.APPROVED)
                .collect(Collectors.toList());
        
        Map<String, Integer> usedLeaves = new HashMap<>();
        Map<String, Integer> totalAllocations = new HashMap<>();
        
        for (LeaveBalanceDTO balance : leaveBalances) {
            String leaveType = balance.getLeaveType();
            int used = 0;
            
            for (LeaveRequestDTO request : approvedLeaveRequests) {
                if (request.getLeaveType().equals(leaveType)) {
                    long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
                    used += days;
                }
            }
            
            usedLeaves.put(leaveType, used);
            totalAllocations.put(leaveType, balance.getBalance() + used);
        }
        
        model.addAttribute("usedLeaves", usedLeaves);
        model.addAttribute("totalAllocations", totalAllocations);
        
        return "leave/balance";
    }
}
