package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class StudentTestAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    private LocalDateTime startedAt   = LocalDateTime.now();
    private LocalDateTime submittedAt;

    // Proctoring violation counters
    private int tabSwitchCount    = 0;
    private int faceViolationCount = 0;
    private int micViolationCount  = 0;

    private boolean forcedEnd      = false;
    private String  forceEndReason;
    private int     totalScore     = 0;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    private List<QuestionSubmission> submissions;
}
