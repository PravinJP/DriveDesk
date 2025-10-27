package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.DriveAnnouncementRequest;
import com.project.DriveDesk.DTO.DriveAnnouncementResponse;
import com.project.DriveDesk.Repository.StudentRepository;
import com.project.DriveDesk.service.DriveAnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class DriveAnnouncementController {

    private final DriveAnnouncementService announcementService;
    private final StudentRepository studentRepository;

    @PostMapping("/teacher")
    public ResponseEntity<DriveAnnouncementResponse> createAnnouncement(
            @RequestBody DriveAnnouncementRequest request) {
        String teacherUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(announcementService.createAnnouncement(request, teacherUsername));
    }

    @GetMapping("/student")
    public ResponseEntity<Page<DriveAnnouncementResponse>> getAnnouncementsForStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String department = studentRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Student not found"))
                .getDepartment();

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(announcementService.getAnnouncementsForStudent(department, pageable));
    }
}

