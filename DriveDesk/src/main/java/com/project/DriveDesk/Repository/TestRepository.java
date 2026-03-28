package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByCreatedByTeacherId(Long teacherId);
}
