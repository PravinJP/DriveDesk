package com.project.DriveDesk.DTO;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDTO {
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String constraints;
    private String sampleInput;
    private String sampleOutput;
    private int marks;
    private List<TestCaseDTO> hiddenTestCases;
}

