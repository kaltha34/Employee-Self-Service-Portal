package com.essp.dto;

import com.essp.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    
    private Long empId;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    private String phone;
    
    private String department;
    
    private Employee.Role role;
    
    // Don't include password in DTO responses
    private String password;
    
    // Constructor to convert Entity to DTO
    public EmployeeDTO(Employee employee) {
        this.empId = employee.getEmpId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.email = employee.getEmail();
        this.phone = employee.getPhone();
        this.department = employee.getDepartment();
        this.role = employee.getRole();
    }
}
