package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class TeacherLoginRequest {
    private String teacherId;   // Teacher unique ID
    private String department;  // Department (for extra validation)
    private String password;    // Password
}