package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.Student;
import com.project.DriveDesk.Models.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface TestSessionRepository extends JpaRepository<TestSession, Long> {
    Optional<TestSession> findBySessionToken(String sessionToken);
    List<TestSession> findByStudent(Student student);
}

