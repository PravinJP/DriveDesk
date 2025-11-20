package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class HiddenTestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String input;
    private String expectedOutput;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}

