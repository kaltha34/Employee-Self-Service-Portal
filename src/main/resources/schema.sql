-- Create EMPLOYEES table
CREATE TABLE EMPLOYEES (
    emp_id NUMBER PRIMARY KEY,
    first_name VARCHAR2(100) NOT NULL,
    last_name VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    phone VARCHAR2(20),
    department VARCHAR2(100),
    role VARCHAR2(20) NOT NULL,
    password VARCHAR2(100) NOT NULL
);

-- Create sequence for EMPLOYEES table
CREATE SEQUENCE emp_id_seq START WITH 1 INCREMENT BY 1;

-- Create trigger for auto-incrementing emp_id
CREATE OR REPLACE TRIGGER emp_id_trigger
BEFORE INSERT ON EMPLOYEES
FOR EACH ROW
BEGIN
    SELECT emp_id_seq.NEXTVAL INTO :NEW.emp_id FROM dual;
END;
/

-- Create LEAVE_REQUESTS table
CREATE TABLE LEAVE_REQUESTS (
    request_id NUMBER PRIMARY KEY,
    emp_id NUMBER NOT NULL,
    leave_type VARCHAR2(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR2(500),
    status VARCHAR2(20) DEFAULT 'PENDING' NOT NULL,
    approver_id NUMBER,
    submitted_on DATE DEFAULT SYSDATE NOT NULL,
    document_path VARCHAR2(255),
    comments VARCHAR2(500),
    CONSTRAINT fk_leave_emp FOREIGN KEY (emp_id) REFERENCES EMPLOYEES(emp_id),
    CONSTRAINT fk_leave_approver FOREIGN KEY (approver_id) REFERENCES EMPLOYEES(emp_id)
);

-- Create sequence for LEAVE_REQUESTS table
CREATE SEQUENCE request_id_seq START WITH 1 INCREMENT BY 1;

-- Create trigger for auto-incrementing request_id
CREATE OR REPLACE TRIGGER request_id_trigger
BEFORE INSERT ON LEAVE_REQUESTS
FOR EACH ROW
BEGIN
    SELECT request_id_seq.NEXTVAL INTO :NEW.request_id FROM dual;
END;
/

-- Create LEAVE_BALANCES table
CREATE TABLE LEAVE_BALANCES (
    id NUMBER PRIMARY KEY,
    emp_id NUMBER NOT NULL,
    leave_type VARCHAR2(50) NOT NULL,
    balance NUMBER NOT NULL,
    CONSTRAINT fk_balance_emp FOREIGN KEY (emp_id) REFERENCES EMPLOYEES(emp_id),
    CONSTRAINT uk_emp_leave_type UNIQUE (emp_id, leave_type)
);

-- Create sequence for LEAVE_BALANCES table
CREATE SEQUENCE balance_id_seq START WITH 1 INCREMENT BY 1;

-- Create trigger for auto-incrementing id
CREATE OR REPLACE TRIGGER balance_id_trigger
BEFORE INSERT ON LEAVE_BALANCES
FOR EACH ROW
BEGIN
    SELECT balance_id_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

-- Create indexes for better performance
CREATE INDEX idx_leave_emp_id ON LEAVE_REQUESTS(emp_id);
CREATE INDEX idx_leave_status ON LEAVE_REQUESTS(status);
CREATE INDEX idx_leave_dates ON LEAVE_REQUESTS(start_date, end_date);
CREATE INDEX idx_balance_emp_id ON LEAVE_BALANCES(emp_id);

-- Create stored procedure for calculating leave duration
CREATE OR REPLACE FUNCTION calculate_leave_duration(
    p_start_date IN DATE,
    p_end_date IN DATE
) RETURN NUMBER IS
    v_duration NUMBER;
BEGIN
    v_duration := p_end_date - p_start_date + 1;
    RETURN v_duration;
END;
/

-- Create stored procedure for validating leave balance
CREATE OR REPLACE PROCEDURE validate_leave_balance(
    p_emp_id IN NUMBER,
    p_leave_type IN VARCHAR2,
    p_start_date IN DATE,
    p_end_date IN DATE
) IS
    v_balance NUMBER;
    v_duration NUMBER;
    v_insufficient_balance EXCEPTION;
BEGIN
    -- Calculate leave duration
    v_duration := calculate_leave_duration(p_start_date, p_end_date);
    
    -- Get current balance
    SELECT balance INTO v_balance
    FROM LEAVE_BALANCES
    WHERE emp_id = p_emp_id AND leave_type = p_leave_type;
    
    -- Check if balance is sufficient
    IF v_balance < v_duration THEN
        RAISE v_insufficient_balance;
    END IF;
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20001, 'Leave balance not found for the specified leave type');
    WHEN v_insufficient_balance THEN
        RAISE_APPLICATION_ERROR(-20002, 'Insufficient leave balance');
END;
/

-- Create stored procedure for updating leave balance after approval
CREATE OR REPLACE PROCEDURE update_leave_balance(
    p_request_id IN NUMBER
) IS
    v_emp_id NUMBER;
    v_leave_type VARCHAR2(50);
    v_start_date DATE;
    v_end_date DATE;
    v_duration NUMBER;
BEGIN
    -- Get leave request details
    SELECT emp_id, leave_type, start_date, end_date
    INTO v_emp_id, v_leave_type, v_start_date, v_end_date
    FROM LEAVE_REQUESTS
    WHERE request_id = p_request_id;
    
    -- Calculate leave duration
    v_duration := calculate_leave_duration(v_start_date, v_end_date);
    
    -- Update leave balance
    UPDATE LEAVE_BALANCES
    SET balance = balance - v_duration
    WHERE emp_id = v_emp_id AND leave_type = v_leave_type;
    
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20003, 'Leave request not found');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
