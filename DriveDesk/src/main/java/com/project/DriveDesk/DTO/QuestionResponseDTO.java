package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class QuestionResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String constraints;
    private String sampleInput;
    private String sampleOutput;
}
