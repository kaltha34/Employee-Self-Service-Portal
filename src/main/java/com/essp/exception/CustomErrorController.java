package com.essp.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Get error status
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            // Add status code to model
            model.addAttribute("status", statusCode);
            
            // Add error message based on status code
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error", "Not Found");
                model.addAttribute("message", "The page you are looking for does not exist.");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("error", "Forbidden");
                model.addAttribute("message", "You don't have permission to access this resource.");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("error", "Internal Server Error");
                model.addAttribute("message", "Something went wrong on our end. Please try again later.");
            } else {
                model.addAttribute("error", "Error");
                model.addAttribute("message", "An unexpected error occurred.");
            }
        }
        
        return "error";
    }
}
