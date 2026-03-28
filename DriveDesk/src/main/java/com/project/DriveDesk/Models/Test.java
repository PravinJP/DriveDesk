package com.project.DriveDesk.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private Integer numberOfQuestions;
    private Integer mcqCount;
    private Integer codingCount;
    private Integer duration; // minutes
    private Integer totalMarks;
    private String instructions;

    @Enumerated(EnumType.STRING)
    private TestStatus status = TestStatus.DRAFT;

    private Long createdByTeacherId;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime scheduledAt;

    @JsonIgnore
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<TestQuestionMapping> questionMappings = new ArrayList<>();
}
