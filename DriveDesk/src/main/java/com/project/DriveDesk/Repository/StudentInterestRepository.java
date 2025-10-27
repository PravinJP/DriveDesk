package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.JobDescription;
import com.project.DriveDesk.Models.Student;
import com.project.DriveDesk.Models.StudentInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentInterestRepository extends JpaRepository<StudentInterest, Long> {
    List<StudentInterest> findByJobDescription(JobDescription jd);
    List<StudentInterest> findByStudent(Student student);
    boolean existsByStudentAndJobDescription(Student student, JobDescription jd);
}

