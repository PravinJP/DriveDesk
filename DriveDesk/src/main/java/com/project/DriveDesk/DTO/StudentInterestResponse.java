package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class StudentInterestResponse {
    private Long id;
    private Long jdId;
    private String studentUsername;
    private String studentEmail;
    private String rollNumber;
    private String department;

}
