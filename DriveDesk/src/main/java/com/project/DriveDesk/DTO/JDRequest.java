package com.project.DriveDesk.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JDRequest {

    private String companyName;
    private String role;
    private String eligibilityBranch;
    private Double eligibilityCgpa;
    private LocalDate deadline;
    private String description;
    private String jdPdfUrl; // Optional field


}
