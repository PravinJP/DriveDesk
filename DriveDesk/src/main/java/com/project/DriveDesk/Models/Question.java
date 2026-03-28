package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType; // MCQ or CODING

    private int marks;

    // ── Coding fields ──────────────────────────────
    private String title;
    @Column(length = 5000)
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String constraints;
    private String sampleInput;
    private String sampleOutput;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HiddenTestCase> hiddenTestCases;

    // ── MCQ fields ─────────────────────────────────
    @Column(length = 2000)
    private String questionText;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<McqOption> options;

    private Integer correctOptionIndex; // 0-based, NOT sent to students
}
