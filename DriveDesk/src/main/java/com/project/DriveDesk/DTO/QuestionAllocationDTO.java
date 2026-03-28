package com.project.DriveDesk.DTO;

import lombok.Data;
import java.util.List;

@Data
public class QuestionAllocationDTO {
    private Long             testId;
    private List<QuestionDTO> questions;
}
