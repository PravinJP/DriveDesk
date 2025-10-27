package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.JDRequest;
import com.project.DriveDesk.DTO.JobDescriptionResponse;
import com.project.DriveDesk.service.JDService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jd")
@RequiredArgsConstructor
public class JDController {

    private final JDService jdService;

    // 🔹 Create JD (auto-inject teacher username)
    @PostMapping("/create")
    public ResponseEntity<JobDescriptionResponse> createJD(@RequestBody JDRequest request) {
        return ResponseEntity.ok(jdService.createJD(request));
    }

    // 🔹 Get JDs posted by logged-in teacher
    @GetMapping("/teacher")
    public ResponseEntity<List<JobDescriptionResponse>> getJDsByTeacher() {
        return ResponseEntity.ok(jdService.getAllJDsByTeacher());
    }

    // 🔹 Update JD
    @PutMapping("/update/{jdId}")
    public ResponseEntity<JobDescriptionResponse> updateJD(@PathVariable Long jdId,
                                                           @RequestBody JDRequest request) {
        return ResponseEntity.ok(jdService.updateJD(jdId, request));
    }

    // 🔹 Delete JD
    @DeleteMapping("/delete/{jdId}")
    public ResponseEntity<Void> deleteJD(@PathVariable Long jdId) {
        jdService.deleteJD(jdId);
        return ResponseEntity.noContent().build();
    }



    // 🔹 Get all JDs (optional public endpoint)
    @GetMapping("/student")
    public ResponseEntity<Page<JobDescriptionResponse>> getAllJDsForStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(jdService.getAllJDs(pageable));
    }

}
