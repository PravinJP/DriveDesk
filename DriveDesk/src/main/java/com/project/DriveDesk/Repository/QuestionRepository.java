package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
