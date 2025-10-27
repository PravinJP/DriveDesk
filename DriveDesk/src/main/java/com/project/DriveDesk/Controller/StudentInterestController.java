package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.StudentInterestRequest;
import com.project.DriveDesk.DTO.StudentInterestResponse;
import com.project.DriveDesk.service.StudentInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
public class StudentInterestController {

    private final StudentInterestService interestService;

    @PostMapping("/register")
    public ResponseEntity<StudentInterestResponse> registerInterest(@RequestBody StudentInterestRequest request) {
        String studentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(interestService.registerInterest(studentUsername, request));
    }

    @GetMapping("/jd/{jdId}")
    public ResponseEntity<List<StudentInterestResponse>> getInterestedStudents(@PathVariable Long jdId) {
        return ResponseEntity.ok(interestService.getInterestedStudents(jdId));
    }

    @GetMapping("/student")
    public ResponseEntity<List<StudentInterestResponse>> getMyInterests() {
        String studentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(interestService.getMyInterests(studentUsername));
    }
}
