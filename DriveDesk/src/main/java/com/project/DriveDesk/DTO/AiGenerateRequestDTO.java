package com.project.DriveDesk.DTO;

import lombok.Data;

/**
 * Request body for AI question generation (Gemini)
 */
@Data
public class AiGenerateRequestDTO {

    private Long testId;

    private String mcqTopic;
    private String codingTopic;

    private Integer mcqCount;
    private Integer codingCount;

    private String difficulty; // easy | medium | hard
}