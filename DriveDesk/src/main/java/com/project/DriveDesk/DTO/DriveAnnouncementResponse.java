package com.project.DriveDesk.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DriveAnnouncementResponse {
    private Long id;
    private String title;
    private String description;
    private String department;
    private String postedByUsername;
    private String postedByEmail;
    private LocalDate driveDate;
}
