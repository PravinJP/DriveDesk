package com.project.DriveDesk.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentCodeSubmissionDTO {
    private String code;
    private String language;       // e.g., "java", "python3", "cpp14"
    private String versionIndex;   // e.g., "4", "3", "3"

    // Getters and setters
}
