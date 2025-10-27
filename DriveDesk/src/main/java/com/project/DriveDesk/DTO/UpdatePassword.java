package com.project.DriveDesk.DTO;

import lombok.Data;

@Data
public class UpdatePassword {
    private String oldPassword;
    private String newPassword;
}
