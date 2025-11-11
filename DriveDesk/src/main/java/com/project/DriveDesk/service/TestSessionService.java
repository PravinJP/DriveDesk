package com.project.DriveDesk.service;

import com.project.DriveDesk.Models.SessionStatus;
import com.project.DriveDesk.Models.Student;
import com.project.DriveDesk.Models.TestSession;
import com.project.DriveDesk.Repository.StudentRepository;
import com.project.DriveDesk.Repository.TestSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestSessionService {

    private final TestSessionRepository sessionRepository;
    private final StudentRepository studentRepository;

    public String startTestSession(String studentUsername, Long testId) {
        Student student = studentRepository.findByUser_Username(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        TestSession session = new TestSession();
        session.setSessionToken(UUID.randomUUID().toString());
        session.setStudent(student);
        session.setTestId(testId);
        session.setStartedAt(LocalDateTime.now());
        session.setStatus(SessionStatus.ACTIVE);
        session.setTabSwitchCount(0);
        session.setFaceViolationCount(0);

        sessionRepository.save(session);
        return session.getSessionToken();
    }

    public void reportViolation(String sessionToken, boolean isTabSwitch, boolean isFaceViolation) {
        TestSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getStatus() != SessionStatus.ACTIVE) return;

        if (isTabSwitch || isFaceViolation) {
            session.setStatus(SessionStatus.TERMINATED);
            session.setEndedAt(LocalDateTime.now());

            String reason = isTabSwitch ? "Tab switch detected" : "Multiple faces detected";
            session.setTerminationReason(reason); // Add this field to your entity if not present
        }

        sessionRepository.save(session);
    }


    public void completeTest(String sessionToken) {
        TestSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getStatus() == SessionStatus.ACTIVE) {
            session.setStatus(SessionStatus.COMPLETED);
            session.setEndedAt(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }
}

