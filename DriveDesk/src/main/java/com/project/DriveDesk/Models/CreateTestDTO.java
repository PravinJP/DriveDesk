package com.project.DriveDesk.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTestDTO {
    private String title;
    private int numberOfQuestions;
    private int duration; // in minutes
    private int totalMarks;
    private String instructions;
}

