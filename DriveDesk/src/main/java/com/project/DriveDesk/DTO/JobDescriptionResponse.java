package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class JobDescriptionResponse {
    private Long id;
    private String companyName;
    private String role;
    private String eligibilityBranch;
    private Double eligibilityCgpa;
    private String deadline;
    private String description;
    private String department;
    private String jdPdfUrl;
    private String postedByUsername;
}
