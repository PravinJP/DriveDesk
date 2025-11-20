package com.project.DriveDesk.service;

import com.project.DriveDesk.DTO.TestCaseResultDTO;
import com.project.DriveDesk.Models.HiddenTestCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeValidationService {

    @Autowired
    private JDoodleClient jdoodleClient;

    public List<TestCaseResultDTO> validate(String code, String language, String versionIndex, List<HiddenTestCase> testCases) {
        return testCases.stream().map(tc -> {
            var response = jdoodleClient.execute(code, tc.getInput(), language, versionIndex);
            String actual = response.getOutput().trim();
            String expected = tc.getExpectedOutput().trim();
            boolean passed = actual.equals(expected);

            String feedback = generateFeedback(actual, expected);

            return new TestCaseResultDTO(tc.getId(), passed, actual, expected, feedback);
        }).collect(Collectors.toList());
    }
    private String generateFeedback(String actual, String expected) {
        if (actual.contains("SyntaxError") || actual.contains("error") || actual.contains("Exception")) {
            return "Your code has a syntax or runtime error. Please check for missing symbols or invalid logic.";
        }
        if (!actual.equals(expected)) {
            return "Your output doesn't match the expected result. Double-check your logic and input handling.";
        }
        return "Great job! Your code passed this test case.";
    }


}
