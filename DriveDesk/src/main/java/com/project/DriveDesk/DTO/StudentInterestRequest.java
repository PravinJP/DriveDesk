package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class StudentInterestRequest {
    private Long jdId;
    private String resumeUrl; // URL or file path after upload
}

