package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.JobDescription;
import com.project.DriveDesk.Models.Student;
import com.project.DriveDesk.Models.StudentInterest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentInterestRepository extends JpaRepository<StudentInterest, Long> {
    List<StudentInterest> findByJobDescription(JobDescription jd);
    List<StudentInterest> findByStudent(Student student);
    boolean existsByStudentAndJobDescription(Student student, JobDescription jd);
    @Modifying
    @Transactional
    @Query("DELETE FROM StudentInterest WHERE jobDescription.id = :jdId")
    void deleteByJobDescriptionId(@Param("jdId") Long jdId);
}

