package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class StudentLoginRequest {
    private String rollNumber;  // Student unique roll number
    private String department;  // Department (for extra validation)
    private String password;    // Password
}

