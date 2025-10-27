package com.project.DriveDesk.DTO;

import jakarta.validation.constraints.NotBlank;

public class AdminLoginRequest {
    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }
    @NotBlank
    public String getUserName() {
        return userName;
    }

    public void setUserName(@NotBlank String userName) {
        this.userName = userName;
    }
}
