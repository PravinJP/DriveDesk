package com.project.DriveDesk.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DriveAnnouncementRequest {
    private String title;
    private String description;
    private LocalDate driveDate;
}
