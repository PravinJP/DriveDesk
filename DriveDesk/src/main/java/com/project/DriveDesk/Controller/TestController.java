package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.*;
import com.project.DriveDesk.Models.*;
import com.project.DriveDesk.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor

public class TestController {

    private final TestService testService;

    @PostMapping("/create-metadata")
    public ResponseEntity<Long> createTestMetadata(@RequestBody CreateTestDTO dto) {
        return ResponseEntity.ok(testService.createTestMetadata(dto));
    }

    @PostMapping("/allocate-questions")
    public ResponseEntity<String> allocateQuestions(@RequestBody QuestionAllocationDTO dto) {
        testService.allocateQuestionsToTest(dto);
        return ResponseEntity.ok("Questions allocated successfully");
    }

    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<Test>> getTestsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(testService.getTestsByTeacher(teacherId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Test>> getAllTests() {
        return ResponseEntity.ok(testService.getAllTests());
    }

    @GetMapping("/{testId}/questions")
    public ResponseEntity<Map<String, Object>> getTestWithQuestions(@PathVariable Long testId) {
        return ResponseEntity.ok(testService.getTestWithQuestions(testId));
    }

    // UPDATED: now checks if student already completed test before starting
    @PostMapping("/{testId}/start")
    public ResponseEntity<?> startAttempt(
            @PathVariable Long testId,
            @RequestParam Long studentId) {
        try {
            Long attemptId = testService.startAttempt(testId, studentId);
            return ResponseEntity.ok(attemptId);
        } catch (IllegalStateException e) {
            // Student already completed this test
            return ResponseEntity.status(409).body(Map.of(
                "error", "ALREADY_COMPLETED",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitTest(@RequestBody SubmitTestDTO dto) {
        return ResponseEntity.ok(testService.submitTest(dto));
    }

    @PostMapping("/proctoring/violation")
    public ResponseEntity<Void> recordViolation(@RequestBody ProctoringViolationDTO dto) {
        testService.recordViolation(dto);
        return ResponseEntity.ok().build();
    }

    // NEW: check if student already completed a specific test
    @GetMapping("/{testId}/student/{studentId}/status")
    public ResponseEntity<Map<String, Object>> getStudentTestStatus(
            @PathVariable Long testId,
            @PathVariable Long studentId) {
        Map<String, Object> status = testService.getStudentTestStatus(testId, studentId);
        return ResponseEntity.ok(status);
    }

    // NEW: get all completed test IDs for a student (for dashboard badges)
    @GetMapping("/completed-by-student/{studentId}")
    public ResponseEntity<List<Long>> getCompletedTestIds(@PathVariable Long studentId) {
        return ResponseEntity.ok(testService.getCompletedTestIds(studentId));
    }
}
