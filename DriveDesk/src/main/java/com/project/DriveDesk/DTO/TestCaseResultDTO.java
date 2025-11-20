package com.project.DriveDesk.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseResultDTO {
    private Long testCaseId;
    private boolean passed;
    private String actualOutput;
    private String expectedOutput;
    private String feedbackMessage;





    // Constructor, getters, setters
}
