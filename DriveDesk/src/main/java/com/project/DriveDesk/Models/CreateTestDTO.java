package com.project.DriveDesk.Models;

import lombok.Data;

@Data
public class CreateTestDTO {
    private String title;
    private Integer mcqCount;
    private Integer codingCount;
    private Integer duration;       // minutes
    private Integer totalMarks;
    private String instructions;
    private Long createdByTeacherId;
    private String scheduledAt; // ISO-8601 string, optional
}
