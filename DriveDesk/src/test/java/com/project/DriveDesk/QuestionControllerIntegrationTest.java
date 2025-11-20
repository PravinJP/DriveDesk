package com.project.DriveDesk;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.DriveDesk.DTO.QuestionDTO;
import com.project.DriveDesk.DTO.TestCaseDTO;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(classes = DriveDeskApplication.class)
@AutoConfigureMockMvc
public class QuestionControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUploadQuestion() throws Exception {
        QuestionDTO dto = new QuestionDTO();
        dto.setTitle("Sum of Array");
        dto.setDescription("Return the sum of all elements in the array.");
        dto.setInputFormat("An integer n followed by n space-separated integers.");
        dto.setOutputFormat("A single integer.");
        dto.setConstraints("1 ≤ n ≤ 10^6, -10^9 ≤ ai ≤ 10^9. Time: O(n), Space: O(1)");
        dto.setSampleInput("5\n1 2 3 4 5");
        dto.setSampleOutput("15");

        TestCaseDTO tc1 = new TestCaseDTO();
        tc1.setInput("3\n1 2 3");
        tc1.setExpectedOutput("6");

        dto.setHiddenTestCases(List.of(tc1));

        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sum of Array"))
                .andExpect(jsonPath("$.description").value("Return the sum of all elements in the array."));
    }

    @Test
    public void testListQuestions() throws Exception {
        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
