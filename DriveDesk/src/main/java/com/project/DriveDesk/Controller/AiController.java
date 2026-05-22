package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.AiGenerateRequestDTO;
import com.project.DriveDesk.service.AiQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoint: POST /api/ai/generate-questions
 * Teacher sends topics → Claude generates questions → saved to DB → test published.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class AiController {

    private final AiQuestionService aiQuestionService;

    @PostMapping("/generate-questions")
    public ResponseEntity<Map<String, Object>> generateQuestions(
            @RequestBody AiGenerateRequestDTO dto) {
        Map<String, Object> result = aiQuestionService.generateAndSaveQuestions(dto);
        return ResponseEntity.ok(result);
    }
}
