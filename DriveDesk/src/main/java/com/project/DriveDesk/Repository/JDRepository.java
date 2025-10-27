package com.project.DriveDesk.Repository;


import com.project.DriveDesk.Models.JobDescription;
import com.project.DriveDesk.Models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JDRepository extends JpaRepository<JobDescription, Long> {
    List<JobDescription> findByPostedBy(Teacher teacher);

    List<JobDescription> findByDepartment(String department);
}
