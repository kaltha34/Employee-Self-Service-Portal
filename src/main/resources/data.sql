-- Insert admin user
INSERT INTO EMPLOYEES (emp_id, first_name, last_name, email, phone, department, role, password)
VALUES (1, 'Admin', 'User', 'admin@essp.com', '1234567890', 'HR', 'ADMIN', '$2a$10$EqKcp1WBKSvUzOCYRZRrXOdJGiGj1S1XcmI.rVnPFcbOFvuBH.n9e');

-- Insert regular employees
INSERT INTO EMPLOYEES (emp_id, first_name, last_name, email, phone, department, role, password)
VALUES (2, 'John', 'Doe', 'john.doe@essp.com', '9876543210', 'IT', 'EMPLOYEE', '$2a$10$EqKcp1WBKSvUzOCYRZRrXOdJGiGj1S1XcmI.rVnPFcbOFvuBH.n9e');

INSERT INTO EMPLOYEES (emp_id, first_name, last_name, email, phone, department, role, password)
VALUES (3, 'Jane', 'Smith', 'jane.smith@essp.com', '5555555555', 'Finance', 'EMPLOYEE', '$2a$10$EqKcp1WBKSvUzOCYRZRrXOdJGiGj1S1XcmI.rVnPFcbOFvuBH.n9e');

INSERT INTO EMPLOYEES (emp_id, first_name, last_name, email, phone, department, role, password)
VALUES (4, 'Bob', 'Johnson', 'bob.johnson@essp.com', '1111111111', 'Marketing', 'EMPLOYEE', '$2a$10$EqKcp1WBKSvUzOCYRZRrXOdJGiGj1S1XcmI.rVnPFcbOFvuBH.n9e');

-- Insert leave balances for employees
-- Annual Leave
INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (1, 1, 'Annual', 20);

INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (2, 2, 'Annual', 20);

INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (3, 3, 'Annual', 20);

INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (4, 4, 'Annual', 20);

-- Sick Leave
INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (5, 1, 'Sick', 10);

INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (6, 2, 'Sick', 10);

INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (7, 3, 'Sick', 10);

INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (8, 4, 'Sick', 10);

-- Casual Leave
INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (9, 1, 'Casual', 7);

INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (10, 2, 'Casual', 7);

INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (11, 3, 'Casual', 7);

INSERT INTO LEAVE_BALANCES (id, emp_id, leave_type, balance)
VALUES (12, 4, 'Casual', 7);

-- Insert some sample leave requests
INSERT INTO LEAVE_REQUESTS (request_id, emp_id, leave_type, start_date, end_date, reason, status, submitted_on)
VALUES (1, 2, 'Annual', '2025-06-01', '2025-06-05', 'Family vacation', 'PENDING', '2025-05-01');

INSERT INTO LEAVE_REQUESTS (request_id, emp_id, leave_type, start_date, end_date, reason, status, approver_id, submitted_on)
VALUES (2, 3, 'Sick', '2025-05-10', '2025-05-12', 'Fever', 'APPROVED', 1, '2025-05-05');

INSERT INTO LEAVE_REQUESTS (request_id, emp_id, leave_type, start_date, end_date, reason, status, approver_id, comments, submitted_on)
VALUES (3, 4, 'Casual', '2025-05-15', '2025-05-15', 'Personal work', 'REJECTED', 1, 'High workload on that day', '2025-05-08');

-- Note: All passwords are 'password' encrypted with BCrypt
