package com.project.DriveDesk.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;   // for Admin login

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppRole role;  // ADMIN, TEACHER, STUDENT



    // Relations
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Teacher teacher;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Student student;


}
