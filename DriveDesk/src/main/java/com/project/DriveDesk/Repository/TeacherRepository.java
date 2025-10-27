package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.Teacher;
import com.project.DriveDesk.Models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByTeacherIdAndDepartment(String teacherId, String department);
    boolean existsByTeacherId(String teacherId);

    Optional<Teacher> findByUser(Users user);

    Optional<Teacher> findByUser_Username(String username); // ✅ fixed
}