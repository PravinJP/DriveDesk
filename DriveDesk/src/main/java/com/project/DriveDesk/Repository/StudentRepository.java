package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.Student;
import com.project.DriveDesk.Models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumberAndDepartment(String rollNumber, String department);
    boolean existsByRollNumber(String rollNumber);

    Optional<Student> findByUser(Users user);

    Optional<Student> findByUser_Username(String studentUsername);
}