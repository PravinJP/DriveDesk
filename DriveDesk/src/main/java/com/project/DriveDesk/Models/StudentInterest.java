package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class StudentInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private JobDescription jobDescription;

    private String resumeUrl; // Store file path or cloud link

    private LocalDateTime timestamp = LocalDateTime.now();
}

