package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.StudentCodeSubmissionDTO;
import com.project.DriveDesk.DTO.TestCaseResultDTO;
import com.project.DriveDesk.Models.Question;
import com.project.DriveDesk.Repository.QuestionRepository;
import com.project.DriveDesk.service.CodeValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/validate-code")
public class CodeValidationController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CodeValidationService validationService;

    @PostMapping("/{questionId}")
    public ResponseEntity<List<TestCaseResultDTO>> validateCode(
            @PathVariable Long questionId,
            @RequestBody StudentCodeSubmissionDTO submission) {

        Question question = questionRepository.findById(questionId).orElseThrow();
        List<TestCaseResultDTO> results = validationService.validate(
                submission.getCode(),
                submission.getLanguage(),
                submission.getVersionIndex(),
                question.getHiddenTestCases()
        );
        return ResponseEntity.ok(results);
    }

}
