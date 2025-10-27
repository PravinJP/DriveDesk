package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriveAnnouncement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String department;
    private LocalDate driveDate;

    @ManyToOne
    @JoinColumn(name = "posted_by_id")
    private Teacher postedBy;
}

