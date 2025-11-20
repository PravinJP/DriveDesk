package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private int numberOfQuestions;
    private int duration; // in minutes
    private int totalMarks;
    private String instructions;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<TestQuestionMapping> questionMappings;
}

