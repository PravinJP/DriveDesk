package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.QuestionAllocationDTO;
import com.project.DriveDesk.Models.CreateTestDTO;
import com.project.DriveDesk.Models.Test;
import com.project.DriveDesk.service.TestService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/create-metadata")
    public ResponseEntity<Long> createTestMetadata(@RequestBody CreateTestDTO dto) {
        Long testId = testService.createTestMetadata(dto);
        return ResponseEntity.ok(testId);
    }

    @PostMapping("/allocate-questions")
    public ResponseEntity<String> allocateQuestions(@RequestBody QuestionAllocationDTO dto) {
        testService.allocateQuestionsToTest(dto);
        return ResponseEntity.ok("Questions allocated successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Test>> getAllTests() {
        return ResponseEntity.ok(testService.getAllTests());
    }

}

