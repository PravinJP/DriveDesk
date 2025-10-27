package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.PlacementTestLinkResponse;
import com.project.DriveDesk.DTO.TestLinkRequest;
import com.project.DriveDesk.service.TestLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testlink")
@RequiredArgsConstructor
public class TestLinkController {

    private final TestLinkService testLinkService;

    // 🔹 Create test link
    @PostMapping("/create")
    public ResponseEntity<PlacementTestLinkResponse> createTestLink(@RequestBody TestLinkRequest request) {
        String teacherUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        PlacementTestLinkResponse link = testLinkService.createTestLink(request, teacherUsername);
        return ResponseEntity.ok(link);
    }

    @GetMapping("/student")
    public ResponseEntity<Page<PlacementTestLinkResponse>> getTestLinksForStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String department = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(testLinkService.getTestLinksForStudent(department, pageable));
    }


    // 🔹 Get test links posted by logged-in teacher
    @GetMapping("/teacher")
    public ResponseEntity<Page<PlacementTestLinkResponse>> getTestLinksByTeacher(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String teacherUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(testLinkService.getTestLinksByTeacher(teacherUsername, pageable));
    }


    // 🔹 Update test link
    @PutMapping("/update/{linkId}")
    public ResponseEntity<PlacementTestLinkResponse> updateTestLink(@PathVariable Long linkId,
                                                                    @RequestBody TestLinkRequest request) {
        String teacherUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        PlacementTestLinkResponse updated = testLinkService.updateTestLink(linkId, request, teacherUsername);
        return ResponseEntity.ok(updated);
    }

    // 🔹 Delete test link
    @DeleteMapping("/delete/{linkId}")
    public ResponseEntity<Void> deleteTestLink(@PathVariable Long linkId) {
        testLinkService.deleteTestLink(linkId);
        return ResponseEntity.noContent().build();
    }
}
