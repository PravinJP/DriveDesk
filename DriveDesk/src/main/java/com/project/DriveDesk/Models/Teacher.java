package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String teacherId;   // unique teacher ID

    @Column(nullable = false)
    private String department;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
