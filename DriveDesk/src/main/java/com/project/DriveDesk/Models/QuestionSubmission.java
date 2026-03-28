package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class QuestionSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private StudentTestAttempt attempt;

    private Long         questionId;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    // MCQ answer
    private Integer selectedOptionIndex;

    // Coding answer
    @Column(length = 10000)
    private String codeSubmission;
    private String language; // "java" | "python3" | "cpp" | "javascript"

    // Grading result
    private boolean correct;
    private int     scoreAwarded;
    private String  judge0Verdict;   // "Accepted", "Wrong Answer", etc.
    private int     testCasesPassed;
    private int     totalTestCases;
}
