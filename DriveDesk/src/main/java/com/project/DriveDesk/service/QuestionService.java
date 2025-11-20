package com.project.DriveDesk.service;

import com.project.DriveDesk.DTO.QuestionDTO;
import com.project.DriveDesk.DTO.QuestionResponseDTO;
import com.project.DriveDesk.Models.HiddenTestCase;
import com.project.DriveDesk.Models.Question;
import com.project.DriveDesk.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public QuestionResponseDTO toResponseDTO(Question question) {
        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setDescription(question.getDescription());
        dto.setInputFormat(question.getInputFormat());
        dto.setOutputFormat(question.getOutputFormat());
        dto.setConstraints(question.getConstraints());
        dto.setSampleInput(question.getSampleInput());
        dto.setSampleOutput(question.getSampleOutput());
        return dto;
    }
    public QuestionResponseDTO saveQuestion(QuestionDTO dto) {
        Question question = new Question();
        question.setTitle(dto.getTitle());
        question.setDescription(dto.getDescription());
        question.setInputFormat(dto.getInputFormat());
        question.setOutputFormat(dto.getOutputFormat());
        question.setConstraints(dto.getConstraints());
        question.setSampleInput(dto.getSampleInput());
        question.setSampleOutput(dto.getSampleOutput());

        List<HiddenTestCase> testCases = dto.getHiddenTestCases().stream().map(tc -> {
            HiddenTestCase testCase = new HiddenTestCase();
            testCase.setInput(tc.getInput());
            testCase.setExpectedOutput(tc.getExpectedOutput());
            testCase.setQuestion(question);
            return testCase;
        }).collect(Collectors.toList());

        question.setHiddenTestCases(testCases);
        Question saved = questionRepository.save(question);
        return toResponseDTO(saved);
    }



    public List<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll().stream().map(q -> {
            QuestionResponseDTO dto = new QuestionResponseDTO();
            dto.setId(q.getId());
            dto.setTitle(q.getTitle());
            dto.setDescription(q.getDescription());
            dto.setInputFormat(q.getInputFormat());
            dto.setOutputFormat(q.getOutputFormat());
            dto.setConstraints(q.getConstraints());
            dto.setSampleInput(q.getSampleInput());
            dto.setSampleOutput(q.getSampleOutput());
            return dto;
        }).collect(Collectors.toList());
    }

}

