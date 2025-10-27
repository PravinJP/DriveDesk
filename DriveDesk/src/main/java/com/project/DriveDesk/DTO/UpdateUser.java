package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class UpdateUser {
    // Common User fields
    private String username;
    private String email;
    private String password;

    // Student-specific fields
    private String rollNumber;
    private String department;

    // Teacher-specific fields
    private String teacherId;
}
