package com.project.DriveDesk.DTO;

import lombok.Data;
import java.util.List;

@Data
public class SubmitTestDTO {
    private Long            testId;
    private Long            studentId;
    private List<AnswerDTO> answers;

    // Proctoring summary
    private boolean forcedEnd;
    private String  forceEndReason;
    private int     tabSwitchCount;
    private int     faceViolationCount;
    private int     micViolationCount;
}
