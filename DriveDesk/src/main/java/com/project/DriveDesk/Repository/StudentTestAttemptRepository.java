package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.AttemptStatus;
import com.project.DriveDesk.Models.StudentTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentTestAttemptRepository extends JpaRepository<StudentTestAttempt, Long> {

    // For submit lookup
    Optional<StudentTestAttempt> findByTest_IdAndStudentId(Long testId, Long studentId);

    // For results panel (teacher)
    @Query("SELECT a FROM StudentTestAttempt a WHERE a.test.id = :testId")
    List<StudentTestAttempt> findByTestId(@Param("testId") Long testId);

    // Student history
    List<StudentTestAttempt> findByStudentId(Long studentId);

    // NEW: check if student already completed a test (prevent re-attempt)
    @Query("SELECT COUNT(a) > 0 FROM StudentTestAttempt a WHERE a.test.id = :testId AND a.studentId = :studentId AND a.status IN ('SUBMITTED','FORCE_ENDED')")
    boolean hasStudentCompletedTest(@Param("testId") Long testId, @Param("studentId") Long studentId);

    // NEW: get all completed attempts for a student (for student dashboard status)
    @Query("SELECT a.test.id FROM StudentTestAttempt a WHERE a.studentId = :studentId AND a.status IN ('SUBMITTED','FORCE_ENDED')")
    List<Long> findCompletedTestIdsByStudentId(@Param("studentId") Long studentId);
}
