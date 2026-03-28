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

    // ── Teacher: Step 1 – create metadata ──────────────────────────
    @PostMapping("/create-metadata")
    public ResponseEntity<Long> createTestMetadata(@RequestBody CreateTestDTO dto) {
        return ResponseEntity.ok(testService.createTestMetadata(dto));
    }

    // ── Teacher: Step 2 – allocate questions & publish ─────────────
    @PostMapping("/allocate-questions")
    public ResponseEntity<String> allocateQuestions(@RequestBody QuestionAllocationDTO dto) {
        testService.allocateQuestionsToTest(dto);
        return ResponseEntity.ok("Questions allocated and test published successfully");
    }

    // ── Teacher: list own tests ─────────────────────────────────────
    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<Test>> getTestsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(testService.getTestsByTeacher(teacherId));
    }

    // ── Admin: list all tests ───────────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<List<Test>> getAllTests() {
        return ResponseEntity.ok(testService.getAllTests());
    }

    // ── Student: get test + questions (no answers) ──────────────────
    @GetMapping("/{testId}/questions")
    public ResponseEntity<Map<String, Object>> getTestWithQuestions(@PathVariable Long testId) {
        return ResponseEntity.ok(testService.getTestWithQuestions(testId));
    }

    // ── Student: start attempt ──────────────────────────────────────
    @PostMapping("/{testId}/start")
    public ResponseEntity<Long> startAttempt(
            @PathVariable Long testId,
            @RequestParam Long studentId) {
        return ResponseEntity.ok(testService.startAttempt(testId, studentId));
    }

    // ── Student: submit test ────────────────────────────────────────
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitTest(@RequestBody SubmitTestDTO dto) {
        return ResponseEntity.ok(testService.submitTest(dto));
    }

    // ── Proctoring: log violation in real-time ──────────────────────
    @PostMapping("/proctoring/violation")
    public ResponseEntity<Void> recordViolation(@RequestBody ProctoringViolationDTO dto) {
        testService.recordViolation(dto);
        return ResponseEntity.ok().build();
    }
}
