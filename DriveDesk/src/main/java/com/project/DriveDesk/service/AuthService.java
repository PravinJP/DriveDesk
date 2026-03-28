package com.project.DriveDesk.service;

import com.project.DriveDesk.Config.JwtUtils;
import com.project.DriveDesk.DTO.*;
import com.project.DriveDesk.Models.AppRole;
import com.project.DriveDesk.Models.Student;
import com.project.DriveDesk.Models.Teacher;
import com.project.DriveDesk.Models.Users;
import com.project.DriveDesk.Repository.StudentRepository;
import com.project.DriveDesk.Repository.TeacherRepository;
import com.project.DriveDesk.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtil;

    //  Admin Signup
    public ResponseEntity<?> registerAdmin(SignUpRequest signUpRequest) {
        if (userRepository.existsByRole(AppRole.ROLE_ADMIN)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, " Admin already exists!"));
        }

        Users admin = Users.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .role(AppRole.ROLE_ADMIN)
                .build();

        userRepository.save(admin);
        return ResponseEntity.ok(new ApiResponse(true, " Admin registered successfully!"));
    }

    //  Admin Login
    public ResponseEntity<?> loginAdmin(AdminLoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserName(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String token = jwtUtil.generateTokenForAdmin(userDetails.getUsername(), "ROLE_ADMIN");

        UserResponse response = new UserResponse(
                userDetails.getEmail(),
                userDetails.getId(),
                "ROLE_ADMIN",
                token,
                userDetails.getUsername()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(response);
    }

    // ✅ Teacher Login
    public ResponseEntity<?> loginTeacher(TeacherLoginRequest request) {
        Teacher teacher = teacherRepository
                .findByTeacherIdAndDepartment(request.getTeacherId(), request.getDepartment())
                .orElseThrow(() -> new RuntimeException("❌ Teacher not found!"));

        Users user = teacher.getUser();
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "❌ Invalid credentials for Teacher!"));
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                teacher.getTeacherId(), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateTokenForTeacher(teacher);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(Map.of(
                        "success", true,
                        "message", "✅ Teacher logged in successfully!",
                        "token", token
                ));
    }

    // ✅ Student Login
    public ResponseEntity<?> loginStudent(StudentLoginRequest request) {
        Student student = studentRepository
                .findByRollNumberAndDepartment(request.getRollNumber(), request.getDepartment())
                .orElseThrow(() -> new RuntimeException("❌ Student not found!"));

        Users user = student.getUser();
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "❌ Invalid credentials for Student!"));
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                student.getRollNumber(), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateTokenForStudent(student);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(Map.of(
                        "success", true,
                        "message", "✅ Student logged in successfully!",
                        "token", token
                ));
    }

    // ✅ Logout
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = jwtUtil.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse(true, "✅ Logged out successfully!"));
    }

    // ✅ Paginated User Listing
    public ResponseEntity<?> getAllUsers(Pageable pageable) {
        Page<TeacherResponse> teacherResponses = teacherRepository.findAll(pageable)
                .map(teacher -> new TeacherResponse(
                        teacher.getId(),
                        teacher.getTeacherId(),
                        teacher.getDepartment(),
                        teacher.getUser().getUsername(),
                        teacher.getUser().getEmail()
                ));

        Page<StudentResponse> studentResponses = studentRepository.findAll(pageable)
                .map(student -> new StudentResponse(
                        student.getId(),
                        student.getRollNumber(),
                        student.getDepartment(),
                        student.getUser().getUsername(),
                        student.getUser().getEmail()
                ));

        AllUsersResponse response = new AllUsersResponse(teacherResponses, studentResponses);
        return ResponseEntity.ok(response);
    }

    // ✅ Update User Details
    public ResponseEntity<?> updateUserDetails(Long userId, UpdateUser request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ User not found with id " + userId));

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPassword(encoder.encode(request.getPassword()));
        userRepository.save(user);

        if (user.getRole() == AppRole.ROLE_STUDENT) {
            Student student = studentRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("❌ Student details not found!"));
            if (request.getDepartment() != null) student.setDepartment(request.getDepartment());
            if (request.getRollNumber() != null) student.setRollNumber(request.getRollNumber());
            studentRepository.save(student);
        } else if (user.getRole() == AppRole.ROLE_TEACHER) {
            Teacher teacher = teacherRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("❌ Teacher details not found!"));
            if (request.getDepartment() != null) teacher.setDepartment(request.getDepartment());
            if (request.getTeacherId() != null) teacher.setTeacherId(request.getTeacherId());
            teacherRepository.save(teacher);
        }

        return ResponseEntity.ok("✅ User details updated successfully!");
    }
}
