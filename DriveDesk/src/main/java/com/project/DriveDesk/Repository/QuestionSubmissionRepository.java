package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.QuestionSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionSubmissionRepository extends JpaRepository<QuestionSubmission, Long> {
}
