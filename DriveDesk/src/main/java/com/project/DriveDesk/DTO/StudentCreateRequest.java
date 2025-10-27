package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class StudentCreateRequest {
    private String userName;
    private String rollNumber;  // Unique student roll number
    private String email;       // Student email
    private String password;    // Password for login
    private String department;  // Department name
}


