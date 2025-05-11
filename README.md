# Employee Self-Service Portal (ESSP)

A secure, real-time web-based self-service system that allows employees to manage their profiles, apply for leave, view leave history, and check leave balances. Administrators can view employee leave data and approve/reject requests.

## Features

### Employee Features
- View and update personal profile information
- Apply for leave with optional document upload
- View leave history and status
- Check leave balances by type (Annual, Casual, Sick)

### Admin Features
- View all employee profiles
- Manage leave requests (approve/reject)
- View pending leave requests
- Manage leave balances

## Technology Stack

- **Backend**: Java (Spring Boot)
- **Frontend**: Thymeleaf templates
- **Database**: Oracle DB
- **ORM**: Hibernate / JPA
- **Build Tool**: Maven
- **Security**: Spring Security with JWT authentication

## Project Structure

The project follows a standard Spring Boot application structure:

```
src/main/java/com/essp/
├── config/           # Configuration classes
├── controller/       # REST API controllers
├── dto/              # Data Transfer Objects
├── exception/        # Custom exceptions and handlers
├── model/            # Entity classes
├── repository/       # Data access interfaces
├── security/         # Security configurations
└── service/          # Business logic
```

## Database Schema

The application uses the following database tables:

1. **EMPLOYEES** - Stores employee information
2. **LEAVE_REQUESTS** - Tracks leave applications and their status
3. **LEAVE_BALANCES** - Maintains leave balances by type for each employee

## API Endpoints

### Authentication
- `POST /api/login` - User authentication

### Employee Profile
- `GET /api/employee/profile` - View profile
- `PUT /api/employee/profile` - Update profile

### Leave Management
- `POST /api/leave/apply` - Apply for leave
- `GET /api/leave/history` - View applied leaves
- `GET /api/leave/balance` - View current leave balance

### Admin Endpoints
- `GET /api/admin/leaves` - Admin view all leave requests
- `POST /api/admin/leave/approve` - Approve leave request
- `POST /api/admin/leave/reject` - Reject leave request

## Setup Instructions

### Prerequisites
- Java 11 or higher
- Maven
- Oracle Database

### Database Setup
1. Create a new Oracle database for the application
2. Update the database connection details in `application.properties`

### Building and Running
1. Clone the repository
2. Navigate to the project directory
3. Build the project: `mvn clean install`
4. Run the application: `mvn spring-boot:run`
5. Access the application at: `http://localhost:8080/essp`

## Sample Credentials

### Admin User
- Email: admin@essp.com
- Password: password

### Employee Users
- Email: john.doe@essp.com
- Password: password

- Email: jane.smith@essp.com
- Password: password

- Email: bob.johnson@essp.com
- Password: password

## Security Features

- JWT-based authentication
- Role-based access control
- Password hashing with BCrypt
- Session timeout
