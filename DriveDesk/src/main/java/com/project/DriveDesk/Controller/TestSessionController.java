package com.project.DriveDesk.Controller;

import com.project.DriveDesk.service.TestSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestSessionController {

    private final TestSessionService testSessionService;

    @PostMapping("/start")
    public ResponseEntity<String> startTest(@RequestParam Long testId) {
        String studentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String sessionToken = testSessionService.startTestSession(studentUsername, testId);
        return ResponseEntity.ok(sessionToken);
    }

    @PostMapping("/violation")
    public ResponseEntity<?> reportViolation(
            @RequestParam String sessionToken,
            @RequestParam(defaultValue = "false") boolean tabSwitch,
            @RequestParam(defaultValue = "false") boolean faceViolation) {

        testSessionService.reportViolation(sessionToken, tabSwitch, faceViolation);
        return ResponseEntity.ok("Violation recorded");
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeTest(@RequestParam String sessionToken) {
        testSessionService.completeTest(sessionToken);
        return ResponseEntity.ok("Test marked as completed");
    }
}

