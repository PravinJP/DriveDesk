package com.project.DriveDesk.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String userName;
    private String email;
    private String role;
    private String token;

    public UserResponse(String email, Long id, String role, String token, String userName) {
        this.email = email;
        this.id = id;
        this.role = role;
        this.token = token;
        this.userName = userName;
    }
    // JWT token that client will use for further requests


}