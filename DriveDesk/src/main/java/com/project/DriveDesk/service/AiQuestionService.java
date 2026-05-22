package com.project.DriveDesk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.DriveDesk.DTO.*;
import com.project.DriveDesk.Models.*;
import com.project.DriveDesk.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiQuestionService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final TestQuestionMappingRepository mappingRepository;

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    // ─────────────────────────────────────────────
    // MAIN ENTRY
    // ─────────────────────────────────────────────

    @Transactional
    public Map<String, Object> generateAndSaveQuestions(AiGenerateRequestDTO req) {

        Test test = testRepository.findById(req.getTestId())
                .orElseThrow(() -> new RuntimeException("Test not found"));

        List<Map<String, Object>> mcqList = new ArrayList<>();
        List<Map<String, Object>> codingList = new ArrayList<>();

        if (req.getMcqCount() != null && req.getMcqCount() > 0) {
            mcqList = generateMcqQuestions(req.getMcqTopic(), req.getMcqCount(), req.getDifficulty());
            saveMcqQuestions(test, mcqList);
        }

        if (req.getCodingCount() != null && req.getCodingCount() > 0) {
            codingList = generateCodingQuestions(req.getCodingTopic(), req.getCodingCount(), req.getDifficulty());
            saveCodingQuestions(test, codingList);
        }

        test.setStatus(TestStatus.PUBLISHED);
        testRepository.save(test);

        return Map.of(
                "testId", test.getId(),
                "mcqGenerated", mcqList.size(),
                "codingGenerated", codingList.size(),
                "status", "PUBLISHED"
        );
    }

    // ─────────────────────────────────────────────
    // MCQ GENERATION
    // ─────────────────────────────────────────────

    private List<Map<String, Object>> generateMcqQuestions(String topic, int count, String difficulty) {

        String prompt = "Generate " + count + " MCQ questions on " + topic +
                " with difficulty " + (difficulty == null ? "medium" : difficulty) +
                ". Return ONLY JSON array. No explanation.\n" +
                "Format:\n" +
                "[\n" +
                "  {\n" +
                "    \"questionText\": \"...\",\n" +
                "    \"options\": [\"A\",\"B\",\"C\",\"D\"],\n" +
                "    \"correctOptionIndex\": 0\n" +
                "  }\n" +
                "]";

        String raw = callGroq(prompt);
        return parseJson(raw);
    }

    // ─────────────────────────────────────────────
    // CODING GENERATION
    // ─────────────────────────────────────────────

    private List<Map<String, Object>> generateCodingQuestions(String topic, int count, String difficulty) {

        String prompt = "Generate " + count + " coding problems on " + topic +
                " with difficulty " + (difficulty == null ? "medium" : difficulty) + ".\n\n" +

                "STRICT RULES:\n" +
                "1. Return ONLY a valid JSON array.\n" +
                "2. Do NOT include explanation, text, or markdown.\n" +
                "3. Do NOT include extra brackets or trailing commas.\n" +
                "4. Output must start with '[' and end with ']'.\n\n" +

                "FORMAT:\n" +
                "[\n" +
                "  {\n" +
                "    \"title\": \"...\",\n" +
                "    \"description\": \"...\",\n" +
                "    \"inputFormat\": \"...\",\n" +
                "    \"outputFormat\": \"...\",\n" +
                "    \"constraints\": \"...\",\n" +
                "    \"sampleInput\": \"...\",\n" +
                "    \"sampleOutput\": \"...\",\n" +
                "    \"marks\": 10\n" +
                "  }\n" +
                "]";

        String raw = callGroq(prompt);
        return parseJson(raw);
    }

    // ─────────────────────────────────────────────
    // GROQ API CALL
    // ─────────────────────────────────────────────

    private String callGroq(String prompt) {
        try {

            Map<String, Object> body = Map.of(
                    "model", "llama-3.1-8b-instant",
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            );

            String requestBody = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + groqApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Groq error: " + response.body());
            }

            JsonNode json = mapper.readTree(response.body());

            return json.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Groq API failed: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // JSON PARSER (FINAL FIX)
    // ─────────────────────────────────────────────

    private List<Map<String, Object>> parseJson(String raw) {
        try {

            String cleaned = raw
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            // ✅ Find first valid JSON array start
            int start = cleaned.indexOf("[");
            if (start == -1) {
                throw new RuntimeException("No JSON array found");
            }

            // ✅ Balance brackets properly
            int bracketCount = 0;
            int end = -1;

            for (int i = start; i < cleaned.length(); i++) {
                if (cleaned.charAt(i) == '[') bracketCount++;
                if (cleaned.charAt(i) == ']') bracketCount--;

                if (bracketCount == 0) {
                    end = i;
                    break;
                }
            }

            if (end == -1) {
                throw new RuntimeException("Unbalanced JSON array");
            }

            String jsonOnly = cleaned.substring(start, end + 1);

            JsonNode arr = mapper.readTree(jsonOnly);

            List<Map<String, Object>> list = new ArrayList<>();
            for (JsonNode node : arr) {
                list.add(mapper.convertValue(node, Map.class));
            }

            return list;

        } catch (Exception e) {
            throw new RuntimeException("Invalid AI response format: " + raw);
        }
    }

    // ─────────────────────────────────────────────
    // SAVE MCQ
    // ─────────────────────────────────────────────

    private void saveMcqQuestions(Test test, List<Map<String, Object>> list) {
        for (Map<String, Object> q : list) {

            Question question = new Question();
            question.setQuestionType(QuestionType.MCQ);
            question.setQuestionText((String) q.get("questionText"));

            Object correctIndexObj = q.get("correctOptionIndex");
            if (correctIndexObj instanceof Number) {
                question.setCorrectOptionIndex(((Number) correctIndexObj).intValue());
            } else {
                question.setCorrectOptionIndex(0);
            }

            Object marksObj = q.getOrDefault("marks", 2);
            if (marksObj instanceof Number) {
                question.setMarks(((Number) marksObj).intValue());
            } else {
                question.setMarks(2);
            }

            List<?> optionsRaw = (List<?>) q.get("options");
            List<McqOption> opts = new ArrayList<>();

            if (optionsRaw != null) {
                for (int i = 0; i < optionsRaw.size(); i++) {

                    Object opt = optionsRaw.get(i);
                    String text = null;

                    if (opt instanceof String) {
                        text = (String) opt;
                    } else if (opt instanceof Map) {
                        Object t = ((Map<?, ?>) opt).get("text");
                        if (t != null) text = t.toString();
                    }

                    if (text == null) continue;

                    McqOption o = new McqOption();
                    o.setOptionText(text);
                    o.setOptionIndex(i);
                    o.setQuestion(question);

                    opts.add(o);
                }
            }

            question.setOptions(opts);
            questionRepository.save(question);

            TestQuestionMapping map = new TestQuestionMapping();
            map.setTest(test);
            map.setQuestion(question);
            map.setMarks(question.getMarks());
            mappingRepository.save(map);
        }
    }

    // ─────────────────────────────────────────────
    // SAVE CODING
    // ─────────────────────────────────────────────

    private void saveCodingQuestions(Test test, List<Map<String, Object>> list) {
        for (Map<String, Object> q : list) {

            Question question = new Question();
            question.setQuestionType(QuestionType.CODING);
            question.setTitle((String) q.get("title"));
            question.setDescription((String) q.get("description"));

            Object marksObj = q.getOrDefault("marks", 10);
            if (marksObj instanceof Number) {
                question.setMarks(((Number) marksObj).intValue());
            } else {
                question.setMarks(10);
            }

            questionRepository.save(question);

            TestQuestionMapping map = new TestQuestionMapping();
            map.setTest(test);
            map.setQuestion(question);
            map.setMarks(question.getMarks());
            mappingRepository.save(map);
        }
    }
}