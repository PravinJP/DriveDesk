package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.*;
import com.project.DriveDesk.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // First Admin signup (only once)
    @PostMapping("/admin-signup")
    public ResponseEntity<?> registerAdmin(@RequestBody SignUpRequest signUpRequest) {
        return authService.registerAdmin(signUpRequest);
    }

    // Admin registers Teacher/Student
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
//        if ("TEACHER".equalsIgnoreCase(signUpRequest.getRoles())) {
//            return authService.registerUser(signUpRequest);
//        } else if ("STUDENT".equalsIgnoreCase(signUpRequest.getRoles())) {
//            return authService.registerUser(signUpRequest);
//        } else {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse(false, "Invalid role. Use TEACHER or STUDENT"));
//        }
//    }

    //  Login (same idea as your ecommerce /signin)
    @PostMapping("/admin/login")
    public ResponseEntity<?> loginAdmin(@RequestBody AdminLoginRequest adminLoginRequest) {
        return authService.loginAdmin(adminLoginRequest);
    }

    // -------------------- Teacher --------------------

    @PostMapping("/teacher/login")
    public ResponseEntity<?> loginTeacher(@RequestBody TeacherLoginRequest request) {
        return authService.loginTeacher(request);
    }

    // -------------------- Student --------------------

    @PostMapping("/student/login")
    public ResponseEntity<?> loginStudent(@RequestBody StudentLoginRequest request) {
        return authService.loginStudent(request);
    }


    //  Logout
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return authService.logout();
    }

    //  Get current user info
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getLoggedInUser(Authentication authentication) {
        return ResponseEntity.ok(new UserResponse(
                authentication.getName(),
                null, // userId (you can fetch from DB if needed)
                authentication.getAuthorities().toString(),
                null,
                authentication.getName()
        ));
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size < 1 || size > 100) {
            return ResponseEntity.badRequest().body("Invalid page or size");
        }
        Pageable pageable = PageRequest.of(page, size);
        return authService.getAllUsers(pageable);
    }




    @PutMapping("/update-user/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUser request
    ) {
        return authService.updateUserDetails(userId,request);
    }
}