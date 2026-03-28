package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.StudentTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentTestAttemptRepository extends JpaRepository<StudentTestAttempt, Long> {
    Optional<StudentTestAttempt> findByTestIdAndStudentId(Long testId, Long studentId);
}
