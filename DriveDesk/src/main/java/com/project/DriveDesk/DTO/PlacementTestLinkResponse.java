package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class PlacementTestLinkResponse {
    private Long id;
    private String title;
    private String link;
    private String department;
    private String testDate;
    private String description;
    private String postedByUsername;
    private String postedByEmail;
}

