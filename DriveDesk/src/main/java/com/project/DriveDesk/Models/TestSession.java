package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionToken; // UUID or JWT

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private Long testId; // Optional: link to test metadata
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    private SessionStatus status; // ACTIVE, TERMINATED, COMPLETED

    // ✅ Proctoring enforcement
    private int tabSwitchCount;
    private int faceViolationCount;

    // ✅ New fields for termination logic
    private String terminationReason; // e.g. "Multiple faces detected", "Tab switch detected"
    private LocalDateTime terminatedAt; // Optional: when termination occurred
}
