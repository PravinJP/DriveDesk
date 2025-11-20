package com.project.DriveDesk.DTO;


import lombok.Data;

@Data
public class JDoodleResponseDTO {
    private String output;
    private int statusCode;
    private String memory;
    private String cpuTime;

    // Getters and setters
}
