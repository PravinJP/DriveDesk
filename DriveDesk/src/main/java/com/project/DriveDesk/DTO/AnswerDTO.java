package com.project.DriveDesk.DTO;

import com.project.DriveDesk.Models.QuestionType;
import lombok.Data;

@Data
public class AnswerDTO {
    private Long         questionId;
    private QuestionType questionType;

    // MCQ
    private Integer selectedOptionIndex;

    // Coding
    private String codeSubmission;
    private String language; // "java" | "python3" | "cpp" | "javascript"
}
