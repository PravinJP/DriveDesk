package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String role;
    private String eligibilityBranch;
    private Double eligibilityCgpa;
    private LocalDate deadline;

    private String description;

    private String department;

    @Column(nullable = true)
    private String jdPdfUrl; // Stores the link or path to the uploaded JD PDF
// Set from teacher's department
    @ManyToOne
    @JoinColumn(name = "posted_by")
    private Teacher postedBy;
}
