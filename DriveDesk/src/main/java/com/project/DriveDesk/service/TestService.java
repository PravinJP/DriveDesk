package com.project.DriveDesk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.DriveDesk.DTO.*;
import com.project.DriveDesk.Models.*;
import com.project.DriveDesk.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository                testRepository;
    private final QuestionRepository            questionRepository;
    private final TestQuestionMappingRepository mappingRepository;
    private final StudentTestAttemptRepository  attemptRepository;
    private final QuestionSubmissionRepository  submissionRepository;

    @Value("${jdoodle.client.id}")
    private String jdoodleClientId;

    @Value("${jdoodle.client.secret}")
    private String jdoodleClientSecret;

    @Value("${jdoodle.api.url:https://api.jdoodle.com/v1/execute}")
    private String jdoodleApiUrl;

    private static final Map<String, String>  LANG_CODES   = Map.of(
        "python3","python3","java","java","cpp","cpp17","javascript","nodejs");
    private static final Map<String, Integer> LANG_VERSION = Map.of(
        "python3",3,"java",4,"cpp",1,"javascript",4);

    // ── CREATE TEST ───────────────────────────────────────────
    public Long createTestMetadata(CreateTestDTO dto) {
        Test test = new Test();
        test.setTitle(dto.getTitle());
        test.setMcqCount(dto.getMcqCount());
        test.setCodingCount(dto.getCodingCount());
        test.setNumberOfQuestions(dto.getMcqCount() + dto.getCodingCount());
        test.setDuration(dto.getDuration());
        test.setTotalMarks(dto.getTotalMarks());
        test.setInstructions(dto.getInstructions());
        test.setCreatedByTeacherId(dto.getCreatedByTeacherId());
        test.setStatus(TestStatus.DRAFT);
        if (dto.getScheduledAt() != null && !dto.getScheduledAt().isBlank())
            test.setScheduledAt(LocalDateTime.parse(dto.getScheduledAt()));
        testRepository.save(test);
        return test.getId();
    }

    @Transactional
    public void allocateQuestionsToTest(QuestionAllocationDTO dto) {
        Test test = testRepository.findById(dto.getTestId())
            .orElseThrow(() -> new RuntimeException("Test not found: " + dto.getTestId()));

        long mcqCount    = dto.getQuestions().stream().filter(q -> q.getQuestionType() == QuestionType.MCQ).count();
        long codingCount = dto.getQuestions().stream().filter(q -> q.getQuestionType() == QuestionType.CODING).count();
        if (mcqCount != test.getMcqCount() || codingCount != test.getCodingCount())
            throw new RuntimeException(String.format(
                "Expected %d MCQ + %d Coding, got %d MCQ + %d Coding",
                test.getMcqCount(), test.getCodingCount(), mcqCount, codingCount));

        for (QuestionDTO qdto : dto.getQuestions()) {
            Question q = new Question();
            q.setQuestionType(qdto.getQuestionType());
            q.setMarks(qdto.getMarks());
            if (qdto.getQuestionType() == QuestionType.MCQ) {
                q.setQuestionText(qdto.getQuestionText());
                q.setCorrectOptionIndex(qdto.getCorrectOptionIndex());
                List<McqOption> opts = new ArrayList<>();
                for (int i = 0; i < qdto.getOptions().size(); i++) {
                    McqOption o = new McqOption();
                    o.setOptionText(qdto.getOptions().get(i));
                    o.setOptionIndex(i);
                    o.setQuestion(q);
                    opts.add(o);
                }
                q.setOptions(opts);
            } else {
                q.setTitle(qdto.getTitle());
                q.setDescription(qdto.getDescription());
                q.setInputFormat(qdto.getInputFormat());
                q.setOutputFormat(qdto.getOutputFormat());
                q.setConstraints(qdto.getConstraints());
                q.setSampleInput(qdto.getSampleInput());
                q.setSampleOutput(qdto.getSampleOutput());
                if (qdto.getHiddenTestCases() != null) {
                    List<HiddenTestCase> tcs = qdto.getHiddenTestCases().stream().map(tc -> {
                        HiddenTestCase h = new HiddenTestCase();
                        h.setInput(tc.getInput());
                        h.setExpectedOutput(tc.getExpectedOutput());
                        h.setQuestion(q);
                        return h;
                    }).collect(Collectors.toList());
                    q.setHiddenTestCases(tcs);
                }
            }
            questionRepository.save(q);
            TestQuestionMapping m = new TestQuestionMapping();
            m.setTest(test); m.setQuestion(q); m.setMarks(qdto.getMarks());
            mappingRepository.save(m);
        }
        test.setStatus(TestStatus.PUBLISHED);
        testRepository.save(test);
    }

    // ── QUERIES ───────────────────────────────────────────────
    public List<Test> getAllTests()                         { return testRepository.findAll(); }
    public List<Test> getTestsByTeacher(Long teacherId)    { return testRepository.findByCreatedByTeacherId(teacherId); }

    public Map<String, Object> getTestWithQuestions(Long testId) {
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test not found"));
        List<Question> questions = mappingRepository.findByTestId(testId)
            .stream().map(TestQuestionMapping::getQuestion).collect(Collectors.toList());

        List<Map<String, Object>> sanitized = questions.stream().map(q -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", q.getId()); m.put("questionType", q.getQuestionType()); m.put("marks", q.getMarks());
            if (q.getQuestionType() == QuestionType.MCQ) {
                m.put("questionText", q.getQuestionText());
                m.put("options", q.getOptions().stream()
                    .sorted(Comparator.comparingInt(McqOption::getOptionIndex))
                    .map(McqOption::getOptionText).collect(Collectors.toList()));
            } else {
                m.put("title",       q.getTitle());
                m.put("description", q.getDescription());
                m.put("inputFormat",  q.getInputFormat());
                m.put("outputFormat", q.getOutputFormat());
                m.put("constraints",  q.getConstraints());
                m.put("sampleInput",  q.getSampleInput());
                m.put("sampleOutput", q.getSampleOutput());
            }
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("test", test); resp.put("questions", sanitized);
        return resp;
    }

    // ── START ATTEMPT (with re-attempt prevention) ────────────
    public Long startAttempt(Long testId, Long studentId) {
        // Prevent re-attempt if already completed
        if (attemptRepository.hasStudentCompletedTest(testId, studentId)) {
            throw new IllegalStateException(
                "You have already completed this test and cannot take it again.");
        }
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test not found"));
        StudentTestAttempt attempt = new StudentTestAttempt();
        attempt.setTest(test);
        attempt.setStudentId(studentId);
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        attempt.setStartedAt(LocalDateTime.now());
        attemptRepository.save(attempt);
        return attempt.getId();
    }

    // ── STUDENT STATUS HELPERS ────────────────────────────────
    public Map<String, Object> getStudentTestStatus(Long testId, Long studentId) {
        Optional<StudentTestAttempt> attempt = attemptRepository.findByTest_IdAndStudentId(testId, studentId);
        Map<String, Object> result = new LinkedHashMap<>();
        if (attempt.isEmpty()) {
            result.put("status", "NOT_STARTED");
            result.put("completed", false);
        } else {
            StudentTestAttempt a = attempt.get();
            result.put("status", a.getStatus().name());
            result.put("completed", a.getStatus() == AttemptStatus.SUBMITTED || a.getStatus() == AttemptStatus.FORCE_ENDED);
            result.put("totalScore", a.getTotalScore());
            result.put("maxScore",   a.getTest().getTotalMarks());
            result.put("percentage", a.getTest().getTotalMarks() > 0
                ? Math.round((double) a.getTotalScore() / a.getTest().getTotalMarks() * 100) : 0);
        }
        return result;
    }

    public List<Long> getCompletedTestIds(Long studentId) {
        return attemptRepository.findCompletedTestIdsByStudentId(studentId);
    }

    // ── SUBMIT TEST ───────────────────────────────────────────
    @Transactional
    public Map<String, Object> submitTest(SubmitTestDTO dto) {
        StudentTestAttempt attempt = attemptRepository
            .findByTest_IdAndStudentId(dto.getTestId(), dto.getStudentId())
            .orElseThrow(() -> new RuntimeException("Attempt not found"));

        attempt.setTabSwitchCount(dto.getTabSwitchCount());
        attempt.setFaceViolationCount(dto.getFaceViolationCount());
        attempt.setMicViolationCount(dto.getMicViolationCount());
        attempt.setForcedEnd(dto.isForcedEnd());
        attempt.setForceEndReason(dto.getForceEndReason());
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setStatus(dto.isForcedEnd() ? AttemptStatus.FORCE_ENDED : AttemptStatus.SUBMITTED);

        int totalScore = 0;
        for (AnswerDTO ans : dto.getAnswers()) {
            Question q = questionRepository.findById(ans.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found: " + ans.getQuestionId()));
            QuestionSubmission sub = new QuestionSubmission();
            sub.setAttempt(attempt); sub.setQuestionId(ans.getQuestionId()); sub.setQuestionType(ans.getQuestionType());
            if (ans.getQuestionType() == QuestionType.MCQ) {
                sub.setSelectedOptionIndex(ans.getSelectedOptionIndex());
                boolean correct = ans.getSelectedOptionIndex() != null
                    && ans.getSelectedOptionIndex().equals(q.getCorrectOptionIndex());
                sub.setCorrect(correct);
                int score = correct ? q.getMarks() : 0;
                sub.setScoreAwarded(score); totalScore += score;
            } else {
                sub.setCodeSubmission(ans.getCodeSubmission()); sub.setLanguage(ans.getLanguage());
                List<HiddenTestCase> tcs = q.getHiddenTestCases();
                int passed = 0;
                for (HiddenTestCase tc : tcs) {
                    JDoodleResult r = runOnJDoodle(ans.getCodeSubmission(), ans.getLanguage(), tc.getInput());
                    if (r.isSuccess() && r.getOutput().trim().equals(tc.getExpectedOutput().trim())) passed++;
                }
                int score = tcs.isEmpty() ? 0 : (int) Math.round(((double) passed / tcs.size()) * q.getMarks());
                sub.setTestCasesPassed(passed); sub.setTotalTestCases(tcs.size());
                sub.setScoreAwarded(score); sub.setCorrect(passed == tcs.size());
                sub.setJudge0Verdict(passed == tcs.size() ? "Accepted" : "Partial/Wrong");
                totalScore += score;
            }
            submissionRepository.save(sub);
        }
        attempt.setTotalScore(totalScore);
        attemptRepository.save(attempt);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("attemptId",  attempt.getId());
        result.put("totalScore", totalScore);
        result.put("maxScore",   attempt.getTest().getTotalMarks());
        result.put("status",     attempt.getStatus());
        return result;
    }

    // ── PROCTORING ────────────────────────────────────────────
    public void recordViolation(ProctoringViolationDTO dto) {
        StudentTestAttempt attempt = attemptRepository.findById(dto.getAttemptId())
            .orElseThrow(() -> new RuntimeException("Attempt not found"));
        switch (dto.getViolationType()) {
            case "TAB_SWITCH"     -> attempt.setTabSwitchCount(attempt.getTabSwitchCount() + 1);
            case "FACE_VIOLATION" -> attempt.setFaceViolationCount(attempt.getFaceViolationCount() + 1);
            case "MIC_VIOLATION"  -> attempt.setMicViolationCount(attempt.getMicViolationCount() + 1);
        }
        attemptRepository.save(attempt);
    }

    // ── JDOODLE ───────────────────────────────────────────────
    private JDoodleResult runOnJDoodle(String code, String language, String stdin) {
        try {
            String  langCode     = LANG_CODES.getOrDefault(language == null ? "python3" : language.toLowerCase(), "python3");
            int     versionIndex = LANG_VERSION.getOrDefault(language == null ? "python3" : language.toLowerCase(), 3);
            String  body         = new ObjectMapper().writeValueAsString(Map.of(
                "clientId", jdoodleClientId, "clientSecret", jdoodleClientSecret,
                "script", code, "language", langCode,
                "versionIndex", String.valueOf(versionIndex), "stdin", stdin == null ? "" : stdin));
            HttpClient  client = HttpClient.newHttpClient();
            HttpRequest req    = HttpRequest.newBuilder().uri(URI.create(jdoodleApiUrl))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode node = new ObjectMapper().readTree(res.body());
            int    status  = node.path("statusCode").asInt(-1);
            String output  = node.path("output").asText("").trim();
            return new JDoodleResult(output, status == 200 && !output.startsWith("JDoodle"));
        } catch (Exception e) { return new JDoodleResult("Error: " + e.getMessage(), false); }
    }

    private static class JDoodleResult {
        private final String output; private final boolean success;
        JDoodleResult(String o, boolean s) { output = o; success = s; }
        String getOutput() { return output; } boolean isSuccess() { return success; }
    }
}
