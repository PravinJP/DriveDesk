package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class TeacherCreateRequest {
    private String userName;
    private String teacherId;   // Unique teacher ID
    private String email;       // Teacher email
    private String password;    // Password for login
    private String department;  // Department name
}
