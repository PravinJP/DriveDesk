package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class JDoodleRequestDTO {
    private String clientId;
    private String clientSecret;
    private String script;
    private String stdin;
    private String language;
    private String versionIndex;

    // Getters and setters
}
