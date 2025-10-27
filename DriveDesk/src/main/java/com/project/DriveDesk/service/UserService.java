package com.project.DriveDesk.service;


import com.project.DriveDesk.DTO.*;
import com.project.DriveDesk.Models.AppRole;
import com.project.DriveDesk.Models.Student;
import com.project.DriveDesk.Models.Users;
import com.project.DriveDesk.Models.Teacher;
import com.project.DriveDesk.Repository.StudentRepository;
import com.project.DriveDesk.Repository.TeacherRepository;
import com.project.DriveDesk.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder encoder;

    /**
     * ✅ Create Teacher (by Admin)
     */
    public ResponseEntity<?> createTeacher(TeacherCreateRequest request) {
        if (teacherRepository.existsByTeacherId(request.getTeacherId())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "❌ Teacher ID already exists!"));
        }

        Users user = Users.builder()
                .username(request.getUserName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(AppRole.ROLE_TEACHER)
                .build();

        Teacher teacher = Teacher.builder()
                .teacherId(request.getTeacherId())
                .department(request.getDepartment())
                .user(user)
                .build();

        user.setTeacher(teacher);

        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "✅ Teacher created successfully!"));
    }

    /**
     * ✅ Create Student (by Admin)
     */
    public ResponseEntity<?> createStudent(StudentCreateRequest request) {
        if (studentRepository.existsByRollNumber(request.getRollNumber())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "❌ Student Roll Number already exists!"));
        }

        Users user = Users.builder()
                .username(request.getUserName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(AppRole.ROLE_STUDENT)
                .build();

        Student student = Student.builder()
                .rollNumber(request.getRollNumber())
                .department(request.getDepartment())
                .user(user)
                .build();

        user.setStudent(student);

        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "✅ Student created successfully!"));
    }

    /**
     * ✅ Delete User
     */
    public ResponseEntity<?> deleteUser(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok(new ApiResponse(true, "✅ User deleted successfully!"));
                })
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "❌ User not found!")));
    }

    public ResponseEntity<?> getUserDetails(Long id) {
        // 1. Try to fetch directly from Users table
        Optional<Users> optionalUser = userRepository.findById(id);

        Users user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            // 2. If not found, check in Teacher table
            Optional<Teacher> teacherOpt = teacherRepository.findById(id);
            if (teacherOpt.isPresent()) {
                user = teacherOpt.get().getUser();
            } else {
                // 3. Else, check in Student table
                Optional<Student> studentOpt = studentRepository.findById(id);
                if (studentOpt.isPresent()) {
                    user = studentOpt.get().getUser();
                } else {
                    throw new RuntimeException("❌ No user/teacher/student found with id " + id);
                }
            }
        }

        // 4. Based on role, return details
        if (user.getRole() == AppRole.ROLE_TEACHER) {
            Teacher teacher = teacherRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("❌ Teacher details not found!"));

            return ResponseEntity.ok(new TeacherResponse(
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    teacher.getTeacherId(),
                    teacher.getDepartment()
            ));
        }

        if (user.getRole() == AppRole.ROLE_STUDENT) {
            Student student = studentRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("❌ Student details not found!"));

            return ResponseEntity.ok(new StudentResponse(
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    student.getRollNumber(),
                    student.getDepartment()
            ));
        }

        return ResponseEntity.badRequest().body("❌ Only Teacher/Student details are available here!");
    }

    public ResponseEntity<?> updateTeacherPassword(Long userId, UpdatePassword request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ User not found with id " + userId));

        if (user.getRole() != AppRole.ROLE_TEACHER) {
            return ResponseEntity.badRequest().body("❌ Not a teacher account!");
        }

        user.setPassword(encoder.encode((CharSequence) request));
        userRepository.save(user);

        return ResponseEntity.ok("✅ Teacher password updated successfully!");
    }

    public ResponseEntity<?> updateStudentPassword(Long userId, UpdatePassword request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ User not found with id " + userId));

        if (user.getRole() != AppRole.ROLE_STUDENT) {
            return ResponseEntity.badRequest().body("❌ Not a student account!");
        }

        user.setPassword(encoder.encode((CharSequence) request));
        userRepository.save(user);

        return ResponseEntity.ok("✅ Student password updated successfully!");
    }




}
