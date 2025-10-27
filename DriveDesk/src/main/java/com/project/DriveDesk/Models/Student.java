package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String rollNumber;   // unique student roll number

    @Column(nullable = false)
    private String department;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}