package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.TestQuestionMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestQuestionMappingRepository extends JpaRepository<TestQuestionMapping, Long> {
    List<TestQuestionMapping> findByTest_Id(Long testId);
}
