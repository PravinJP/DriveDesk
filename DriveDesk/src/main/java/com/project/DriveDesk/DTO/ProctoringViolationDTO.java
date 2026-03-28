package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class ProctoringViolationDTO {
    private Long   attemptId;
    private String violationType; // "TAB_SWITCH" | "FACE_VIOLATION" | "MIC_VIOLATION"
    private String details;
}
