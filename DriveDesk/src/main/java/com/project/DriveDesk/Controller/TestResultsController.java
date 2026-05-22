package com.project.DriveDesk.Controller;

import com.project.DriveDesk.Models.*;
import com.project.DriveDesk.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Endpoints for teacher to view test results / student scores.
 * GET /api/results/test/{testId}  → all student attempts for a test
 * GET /api/results/test/{testId}/summary → aggregate stats
 */
@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestResultsController {

    private final StudentTestAttemptRepository attemptRepository;
    private final TestRepository               testRepository;

    /** All student attempts for a given test (teacher view) */
    @GetMapping("/test/{testId}")
    public ResponseEntity<List<Map<String, Object>>> getResultsForTest(
            @PathVariable Long testId) {

        List<StudentTestAttempt> attempts = attemptRepository.findByTestId(testId);

        List<Map<String, Object>> results = attempts.stream().map(a -> {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("attemptId",         a.getId());
            r.put("studentId",         a.getStudentId());
            r.put("status",            a.getStatus());
            r.put("totalScore",        a.getTotalScore());
            r.put("maxScore",          a.getTest().getTotalMarks());
            r.put("percentage",        a.getTest().getTotalMarks() > 0
                ? Math.round((double) a.getTotalScore() / a.getTest().getTotalMarks() * 100)
                : 0);
            r.put("startedAt",         a.getStartedAt());
            r.put("submittedAt",       a.getSubmittedAt());
            r.put("tabSwitchCount",    a.getTabSwitchCount());
            r.put("faceViolationCount",a.getFaceViolationCount());
            r.put("micViolationCount", a.getMicViolationCount());
            r.put("forcedEnd",         a.isForcedEnd());
            r.put("forceEndReason",    a.getForceEndReason());
            return r;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }

    /** Summary stats for a test */
    @GetMapping("/test/{testId}/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Long testId) {
        List<StudentTestAttempt> attempts = attemptRepository.findByTestId(testId);
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test not found"));

        if (attempts.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "totalAttempts", 0,
                "message", "No students have taken this test yet"
            ));
        }

        List<StudentTestAttempt> submitted = attempts.stream()
            .filter(a -> a.getStatus() == AttemptStatus.SUBMITTED || a.getStatus() == AttemptStatus.FORCE_ENDED)
            .collect(Collectors.toList());

        OptionalDouble avg = submitted.stream().mapToInt(StudentTestAttempt::getTotalScore).average();
        int max = submitted.stream().mapToInt(StudentTestAttempt::getTotalScore).max().orElse(0);
        int min = submitted.stream().mapToInt(StudentTestAttempt::getTotalScore).min().orElse(0);
        long passed = submitted.stream()
            .filter(a -> test.getTotalMarks() > 0
                && (double) a.getTotalScore() / test.getTotalMarks() >= 0.5)
            .count();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("testId",         testId);
        summary.put("testTitle",      test.getTitle());
        summary.put("totalMarks",     test.getTotalMarks());
        summary.put("totalAttempts",  attempts.size());
        summary.put("submitted",      submitted.size());
        summary.put("forcedEnded",    submitted.stream().filter(StudentTestAttempt::isForcedEnd).count());
        summary.put("passCount",      passed);
        summary.put("passRate",       submitted.isEmpty() ? 0 : Math.round((double) passed / submitted.size() * 100));
        summary.put("averageScore",   avg.isPresent() ? Math.round(avg.getAsDouble()) : 0);
        summary.put("highestScore",   max);
        summary.put("lowestScore",    min);
        return ResponseEntity.ok(summary);
    }
}
