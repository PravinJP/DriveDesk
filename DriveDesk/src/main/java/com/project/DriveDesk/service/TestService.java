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

    // ── JDoodle credentials (free tier: 200 runs/day) ─────────────
    // Get yours free at https://www.jdoodle.com/compiler-api/
    @Value("${jdoodle.clientId}")
    private String jdoodleClientId;

    @Value("${jdoodle.clientSecret}")
    private String jdoodleClientSecret;

    @Value("${jdoodle.api.url:https://api.jdoodle.com/v1/execute}")
    private String jdoodleApiUrl;

    // ── JDoodle language codes ─────────────────────────────────────
    // Full list: https://www.jdoodle.com/compiler-api/
    private static final Map<String, String> LANG_CODES = Map.of(
        "python3",    "python3",
        "java",       "java",
        "cpp",        "cpp17",
        "javascript", "nodejs"
    );

    // JDoodle version index per language (usually 0 = latest stable)
    private static final Map<String, Integer> LANG_VERSION = Map.of(
        "python3",    3,
        "java",       4,
        "cpp",        1,
        "javascript", 4
    );

    // ─────────────────────────────────────────────────────────
    // TEACHER: CREATE TEST
    // ─────────────────────────────────────────────────────────

    public Long createTestMetadata(CreateTestDTO dto) {
        Test test = new Test();

        // Basic fields
        test.setTitle(dto.getTitle());
        test.setInstructions(dto.getInstructions());
        test.setCreatedByTeacherId(dto.getCreatedByTeacherId());
        test.setStatus(TestStatus.DRAFT);

        // Safe number handling
        int mcqCount = dto.getMcqCount() != null ? dto.getMcqCount() : 0;
        int codingCount = dto.getCodingCount() != null ? dto.getCodingCount() : 0;
        int duration = dto.getDuration() != null ? dto.getDuration() : 0;
        int totalMarks = dto.getTotalMarks() != null ? dto.getTotalMarks() : 0;

        test.setMcqCount(mcqCount);
        test.setCodingCount(codingCount);
        test.setNumberOfQuestions(mcqCount + codingCount);
        test.setDuration(duration);
        test.setTotalMarks(totalMarks);

        // Safe date parsing
        try {
            if (dto.getScheduledAt() != null && !dto.getScheduledAt().isBlank()) {
                test.setScheduledAt(LocalDateTime.parse(dto.getScheduledAt()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Use yyyy-MM-ddTHH:mm:ss");
        }

        testRepository.save(test);
        return test.getId();
    }

    @Transactional
    public void allocateQuestionsToTest(QuestionAllocationDTO dto) {
        Test test = testRepository.findById(dto.getTestId())
            .orElseThrow(() -> new RuntimeException("Test not found: " + dto.getTestId()));

        long mcqCount    = dto.getQuestions().stream().filter(q -> q.getQuestionType() == QuestionType.MCQ).count();
        long codingCount = dto.getQuestions().stream().filter(q -> q.getQuestionType() == QuestionType.CODING).count();

        if (mcqCount != test.getMcqCount() || codingCount != test.getCodingCount()) {
            throw new RuntimeException(
                String.format("Expected %d MCQ + %d Coding, but received %d MCQ + %d Coding",
                    test.getMcqCount(), test.getCodingCount(), mcqCount, codingCount));
        }

        for (QuestionDTO qdto : dto.getQuestions()) {
            Question question = new Question();
            question.setQuestionType(qdto.getQuestionType());
            question.setMarks(qdto.getMarks());

            if (qdto.getQuestionType() == QuestionType.MCQ) {
                question.setQuestionText(qdto.getQuestionText());
                question.setCorrectOptionIndex(qdto.getCorrectOptionIndex());

                List<McqOption> opts = new ArrayList<>();
                for (int i = 0; i < qdto.getOptions().size(); i++) {
                    McqOption o = new McqOption();
                    o.setOptionText(qdto.getOptions().get(i));
                    o.setOptionIndex(i);
                    o.setQuestion(question);
                    opts.add(o);
                }
                question.setOptions(opts);

            } else {
                question.setTitle(qdto.getTitle());
                question.setDescription(qdto.getDescription());
                question.setInputFormat(qdto.getInputFormat());
                question.setOutputFormat(qdto.getOutputFormat());
                question.setConstraints(qdto.getConstraints());
                question.setSampleInput(qdto.getSampleInput());
                question.setSampleOutput(qdto.getSampleOutput());

                if (qdto.getHiddenTestCases() != null) {
                    List<HiddenTestCase> tcs = qdto.getHiddenTestCases().stream().map(tc -> {
                        HiddenTestCase h = new HiddenTestCase();
                        h.setInput(tc.getInput());
                        h.setExpectedOutput(tc.getExpectedOutput());
                        h.setQuestion(question);
                        return h;
                    }).collect(Collectors.toList());
                    question.setHiddenTestCases(tcs);
                }
            }

            questionRepository.save(question);

            TestQuestionMapping mapping = new TestQuestionMapping();
            mapping.setTest(test);
            mapping.setQuestion(question);
            mapping.setMarks(qdto.getMarks());
            mappingRepository.save(mapping);
        }

        test.setStatus(TestStatus.PUBLISHED);
        testRepository.save(test);
    }

    // ─────────────────────────────────────────────────────────
    // TEACHER: QUERIES
    // ─────────────────────────────────────────────────────────

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public List<Test> getTestsByTeacher(Long teacherId) {
        return testRepository.findByCreatedByTeacherId(teacherId);
    }

    // ─────────────────────────────────────────────────────────
    // STUDENT: GET TEST (no correct answers exposed)
    // ─────────────────────────────────────────────────────────

    public Map<String, Object> getTestWithQuestions(Long testId) {
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test not found"));

        List<Question> questions = mappingRepository.findByTestId(testId)
            .stream().map(TestQuestionMapping::getQuestion).collect(Collectors.toList());

        List<Map<String, Object>> sanitized = questions.stream().map(q -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",           q.getId());
            m.put("questionType", q.getQuestionType());
            m.put("marks",        q.getMarks());
            if (q.getQuestionType() == QuestionType.MCQ) {
                m.put("questionText", q.getQuestionText());
                m.put("options", q.getOptions().stream()
                    .sorted(Comparator.comparingInt(McqOption::getOptionIndex))
                    .map(McqOption::getOptionText)
                    .collect(Collectors.toList()));
                // correctOptionIndex intentionally omitted
            } else {
                m.put("title",        q.getTitle());
                m.put("description",  q.getDescription());
                m.put("inputFormat",  q.getInputFormat());
                m.put("outputFormat", q.getOutputFormat());
                m.put("constraints",  q.getConstraints());
                m.put("sampleInput",  q.getSampleInput());
                m.put("sampleOutput", q.getSampleOutput());
            }
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("test",      test);
        resp.put("questions", sanitized);
        return resp;
    }

    // ─────────────────────────────────────────────────────────
    // STUDENT: START ATTEMPT
    // ─────────────────────────────────────────────────────────

    public Long startAttempt(Long testId, Long studentId) {
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

    // ─────────────────────────────────────────────────────────
    // STUDENT: SUBMIT TEST
    // ─────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> submitTest(SubmitTestDTO dto) {
        StudentTestAttempt attempt = attemptRepository
            .findByTestIdAndStudentId(dto.getTestId(), dto.getStudentId())
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
            sub.setAttempt(attempt);
            sub.setQuestionId(ans.getQuestionId());
            sub.setQuestionType(ans.getQuestionType());

            if (ans.getQuestionType() == QuestionType.MCQ) {
                sub.setSelectedOptionIndex(ans.getSelectedOptionIndex());
                boolean correct = ans.getSelectedOptionIndex() != null
                    && ans.getSelectedOptionIndex().equals(q.getCorrectOptionIndex());
                sub.setCorrect(correct);
                int score = correct ? q.getMarks() : 0;
                sub.setScoreAwarded(score);
                totalScore += score;

            } else {
                sub.setCodeSubmission(ans.getCodeSubmission());
                sub.setLanguage(ans.getLanguage());

                List<HiddenTestCase> tcs = q.getHiddenTestCases();
                int passed = 0;

                for (HiddenTestCase tc : tcs) {
                    JDoodleResult result = runOnJDoodle(
                        ans.getCodeSubmission(),
                        ans.getLanguage(),
                        tc.getInput()
                    );
                    // Trim both sides before comparing to ignore trailing newlines
                    boolean accepted = result.isSuccess()
                        && result.getOutput().trim().equals(tc.getExpectedOutput().trim());
                    if (accepted) passed++;
                }

                int score = tcs.isEmpty() ? 0
                    : (int) Math.round(((double) passed / tcs.size()) * q.getMarks());
                sub.setTestCasesPassed(passed);
                sub.setTotalTestCases(tcs.size());
                sub.setScoreAwarded(score);
                sub.setCorrect(passed == tcs.size());
                sub.setJudge0Verdict(passed == tcs.size() ? "Accepted" : "Partial / Wrong Answer");
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

    // ─────────────────────────────────────────────────────────
    // PROCTORING: LOG VIOLATION
    // ─────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────
    // JDOODLE INTEGRATION
    // Free tier: 200 executions/day
    // Docs: https://www.jdoodle.com/compiler-api/
    // ─────────────────────────────────────────────────────────

    /**
     * Executes code on JDoodle and returns the program output.
     *
     * @param code     Source code string
     * @param language One of: "python3", "java", "cpp", "javascript"
     * @param stdin    Input to feed to the program via stdin
     * @return JDoodleResult containing output and success flag
     */
    private JDoodleResult runOnJDoodle(String code, String language, String stdin) {
        try {
            String langCode = LANG_CODES.getOrDefault(
                language == null ? "python3" : language.toLowerCase(), "python3");
            int versionIndex = LANG_VERSION.getOrDefault(
                language == null ? "python3" : language.toLowerCase(), 3);

            // Build JSON body — JDoodle expects plain JSON (no base64)
            String requestBody = new ObjectMapper().writeValueAsString(Map.of(
                "clientId",     jdoodleClientId,
                "clientSecret", jdoodleClientSecret,
                "script",       code,
                "language",     langCode,
                "versionIndex", String.valueOf(versionIndex),
                "stdin",        stdin == null ? "" : stdin
            ));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jdoodleApiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // JDoodle response shape:
            // { "output": "...", "statusCode": 200, "memory": "...", "cpuTime": "..." }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());

            int statusCode = node.path("statusCode").asInt(-1);
            String output  = node.path("output").asText("").trim();

            // statusCode 200 = successful execution (even if wrong answer)
            // statusCode 400 = compilation/runtime error
            boolean success = (statusCode == 200) && !output.startsWith("JDoodle");

            return new JDoodleResult(output, success);

        } catch (Exception e) {
            // Network error or parse failure — treat as wrong answer, don't crash grading
            return new JDoodleResult("Error: " + e.getMessage(), false);
        }
    }

    // ─────────────────────────────────────────────────────────
    // Simple result wrapper
    // ─────────────────────────────────────────────────────────

    private static class JDoodleResult {
        private final String  output;
        private final boolean success;

        JDoodleResult(String output, boolean success) {
            this.output  = output;
            this.success = success;
        }

        String  getOutput()  { return output; }
        boolean isSuccess()  { return success; }
    }
}
