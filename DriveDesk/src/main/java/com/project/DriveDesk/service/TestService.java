package com.project.DriveDesk.service;

import com.project.DriveDesk.DTO.QuestionAllocationDTO;
import com.project.DriveDesk.DTO.QuestionDTO;
import com.project.DriveDesk.Models.*;
import com.project.DriveDesk.Repository.QuestionRepository;
import com.project.DriveDesk.Repository.TestQuestionMappingRepository;
import com.project.DriveDesk.Repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final TestQuestionMappingRepository mappingRepository;

    public Long createTestMetadata(CreateTestDTO dto) {
        Test test = new Test();
        test.setTitle(dto.getTitle());
        test.setNumberOfQuestions(dto.getNumberOfQuestions());
        test.setDuration(dto.getDuration());
        test.setTotalMarks(dto.getTotalMarks());
        test.setInstructions(dto.getInstructions());
        testRepository.save(test);
        return test.getId();
    }

    public void allocateQuestionsToTest(QuestionAllocationDTO dto) {
        Test test = testRepository.findById(dto.getTestId())
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (dto.getQuestions().size() != test.getNumberOfQuestions()) {
            throw new RuntimeException("Number of questions must match test configuration");
        }

        for (QuestionDTO qdto : dto.getQuestions()) {
            Question question = new Question();
            question.setTitle(qdto.getTitle());
            question.setDescription(qdto.getDescription());
            question.setInputFormat(qdto.getInputFormat());
            question.setOutputFormat(qdto.getOutputFormat());
            question.setConstraints(qdto.getConstraints());
            question.setSampleInput(qdto.getSampleInput());
            question.setSampleOutput(qdto.getSampleOutput());
            question.setMarks(qdto.getMarks());
            List<HiddenTestCase> hiddenTestCases = qdto.getHiddenTestCases().stream().map(tc -> {
                HiddenTestCase entity = new HiddenTestCase();
                entity.setInput(tc.getInput());
                entity.setExpectedOutput(tc.getExpectedOutput());
                entity.setQuestion(question); // link back to parent question
                return entity;
            }).collect(Collectors.toList());

            question.setHiddenTestCases(hiddenTestCases);
// map DTOs to entities
            questionRepository.save(question);

            TestQuestionMapping mapping = new TestQuestionMapping();
            mapping.setTest(test);
            mapping.setQuestion(question);
            mapping.setMarks(qdto.getMarks());
            mappingRepository.save(mapping);
        }
    }
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }
}

