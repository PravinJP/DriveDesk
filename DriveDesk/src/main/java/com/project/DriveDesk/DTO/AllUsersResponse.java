package com.project.DriveDesk.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class AllUsersResponse {
    private Page<TeacherResponse> teachers;
    private Page<StudentResponse> students;
}

