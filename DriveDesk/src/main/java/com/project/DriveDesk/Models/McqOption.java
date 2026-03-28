package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class McqOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String optionText;
    private int optionIndex; // 0-based
}
