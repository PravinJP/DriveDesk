package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.StudentCreateRequest;
import com.project.DriveDesk.DTO.TeacherCreateRequest;
import com.project.DriveDesk.DTO.UpdatePassword;
import com.project.DriveDesk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // -------------------- Create Teacher --------------------
    @PostMapping("/teacher")
    public ResponseEntity<?> createTeacher(@RequestBody TeacherCreateRequest request) {
        return userService.createTeacher(request);
    }

    // -------------------- Create Student --------------------
    @PostMapping("/student")
    public ResponseEntity<?> createStudent(@RequestBody StudentCreateRequest request) {
        return userService.createStudent(request);
    }

    // -------------------- Delete User --------------------
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId) {
        return userService.getUserDetails(userId);
    }



    @PutMapping("/{studentId}/update-password")
    public ResponseEntity<?> updatePasswordStudent(
            @PathVariable Long studentId,
            @RequestBody UpdatePassword request
    ) {
        return userService.updateStudentPassword(studentId,request);
    }
    @PutMapping("/{teacherId}/update-password")
    public ResponseEntity<?> updatePasswordTeacher(
            @PathVariable Long teacherId,
            @RequestBody UpdatePassword request  // ✅ should resolve now
    ) {
        return userService.updateTeacherPassword(teacherId,request);
    }

}
