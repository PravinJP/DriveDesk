package com.project.DriveDesk.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TestLinkRequest {
    private String title;
    private String link;

    private LocalDate testDate;
    private String description;
}

