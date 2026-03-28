package com.project.DriveDesk.DTO;

import com.project.DriveDesk.Models.QuestionType;
import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private QuestionType questionType; // MCQ | CODING
    private int marks;

    // ── MCQ ───────────────────────────────────────
    private String       questionText;
    private List<String> options;            // 4 option texts
    private Integer      correctOptionIndex; // 0-based

    // ── Coding ────────────────────────────────────
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String constraints;
    private String sampleInput;
    private String sampleOutput;
    private List<TestCaseDTO> hiddenTestCases;
}
