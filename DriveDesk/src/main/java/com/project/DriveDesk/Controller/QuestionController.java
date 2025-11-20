package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.QuestionDTO;
import com.project.DriveDesk.DTO.QuestionResponseDTO;
import com.project.DriveDesk.Models.Question;
import com.project.DriveDesk.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponseDTO> uploadQuestion(@RequestBody QuestionDTO dto) {
        QuestionResponseDTO saved = questionService.saveQuestion(dto);
        return ResponseEntity.ok(saved);
    }


    @GetMapping
    public ResponseEntity<List<QuestionResponseDTO>> listQuestions() {
        List<QuestionResponseDTO> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

}
