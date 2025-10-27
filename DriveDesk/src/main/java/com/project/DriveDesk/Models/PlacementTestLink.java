package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PlacementTestLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;           // e.g., "HackerRank Test for AIML"
    private String link;            // e.g., https://hackerrank.com/test/abc123
    private String department;      // Who can access it
    private LocalDate testDate;     // Optional: when the test is scheduled
    private String description;     // Optional instructions

    @ManyToOne
    private Teacher postedBy;
}

