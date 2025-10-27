package com.project.DriveDesk.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private Long id;
    private String rollNumber;
    private String department;
    private String username;
    private String email;
}
