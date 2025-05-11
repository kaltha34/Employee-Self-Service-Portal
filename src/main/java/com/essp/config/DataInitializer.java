package com.essp.config;

import com.essp.model.Employee;
import com.essp.model.LeaveBalance;
import com.essp.model.LeaveRequest;
import com.essp.repository.EmployeeRepository;
import com.essp.repository.LeaveBalanceRepository;
import com.essp.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Profile("!prod")
    public CommandLineRunner initData(EmployeeRepository employeeRepository,
                                     LeaveBalanceRepository leaveBalanceRepository,
                                     LeaveRequestRepository leaveRequestRepository) {
        return args -> {
            // Check if data already exists
            if (employeeRepository.count() > 0) {
                return;
            }

            // Create admin user
            Employee admin = new Employee();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@essp.com");
            admin.setPhone("1234567890");
            admin.setDepartment("HR");
            admin.setRole(Employee.Role.ADMIN);
            admin.setPassword(passwordEncoder.encode("password"));
            employeeRepository.save(admin);

            // Create regular employees
            Employee john = new Employee();
            john.setFirstName("John");
            john.setLastName("Doe");
            john.setEmail("john.doe@essp.com");
            john.setPhone("9876543210");
            john.setDepartment("IT");
            john.setRole(Employee.Role.EMPLOYEE);
            john.setPassword(passwordEncoder.encode("password"));
            employeeRepository.save(john);

            Employee jane = new Employee();
            jane.setFirstName("Jane");
            jane.setLastName("Smith");
            jane.setEmail("jane.smith@essp.com");
            jane.setPhone("5555555555");
            jane.setDepartment("Finance");
            jane.setRole(Employee.Role.EMPLOYEE);
            jane.setPassword(passwordEncoder.encode("password"));
            employeeRepository.save(jane);

            Employee bob = new Employee();
            bob.setFirstName("Bob");
            bob.setLastName("Johnson");
            bob.setEmail("bob.johnson@essp.com");
            bob.setPhone("1111111111");
            bob.setDepartment("Marketing");
            bob.setRole(Employee.Role.EMPLOYEE);
            bob.setPassword(passwordEncoder.encode("password"));
            employeeRepository.save(bob);

            // Create leave balances
            // Annual Leave
            createLeaveBalance(leaveBalanceRepository, admin, "Annual", 20);
            createLeaveBalance(leaveBalanceRepository, john, "Annual", 20);
            createLeaveBalance(leaveBalanceRepository, jane, "Annual", 20);
            createLeaveBalance(leaveBalanceRepository, bob, "Annual", 20);

            // Sick Leave
            createLeaveBalance(leaveBalanceRepository, admin, "Sick", 10);
            createLeaveBalance(leaveBalanceRepository, john, "Sick", 10);
            createLeaveBalance(leaveBalanceRepository, jane, "Sick", 10);
            createLeaveBalance(leaveBalanceRepository, bob, "Sick", 10);

            // Casual Leave
            createLeaveBalance(leaveBalanceRepository, admin, "Casual", 7);
            createLeaveBalance(leaveBalanceRepository, john, "Casual", 7);
            createLeaveBalance(leaveBalanceRepository, jane, "Casual", 7);
            createLeaveBalance(leaveBalanceRepository, bob, "Casual", 7);

            // Create sample leave requests
            LeaveRequest request1 = new LeaveRequest();
            request1.setEmployee(john);
            request1.setLeaveType("Annual");
            request1.setStartDate(LocalDate.now().plusDays(20));
            request1.setEndDate(LocalDate.now().plusDays(24));
            request1.setReason("Family vacation");
            request1.setStatus(LeaveRequest.Status.PENDING);
            request1.setSubmittedOn(LocalDate.now().minusDays(5));
            leaveRequestRepository.save(request1);

            LeaveRequest request2 = new LeaveRequest();
            request2.setEmployee(jane);
            request2.setLeaveType("Sick");
            request2.setStartDate(LocalDate.now().plusDays(1));
            request2.setEndDate(LocalDate.now().plusDays(3));
            request2.setReason("Fever");
            request2.setStatus(LeaveRequest.Status.APPROVED);
            request2.setApprover(admin);
            request2.setSubmittedOn(LocalDate.now().minusDays(2));
            leaveRequestRepository.save(request2);

            LeaveRequest request3 = new LeaveRequest();
            request3.setEmployee(bob);
            request3.setLeaveType("Casual");
            request3.setStartDate(LocalDate.now().plusDays(5));
            request3.setEndDate(LocalDate.now().plusDays(5));
            request3.setReason("Personal work");
            request3.setStatus(LeaveRequest.Status.REJECTED);
            request3.setApprover(admin);
            request3.setComments("High workload on that day");
            request3.setSubmittedOn(LocalDate.now().minusDays(1));
            leaveRequestRepository.save(request3);
        };
    }

    private void createLeaveBalance(LeaveBalanceRepository repository, Employee employee, String leaveType, int balance) {
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setEmployee(employee);
        leaveBalance.setLeaveType(leaveType);
        leaveBalance.setBalance(balance);
        repository.save(leaveBalance);
    }
}
