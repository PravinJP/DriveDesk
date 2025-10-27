package com.project.DriveDesk.service;

import com.project.DriveDesk.DTO.PlacementTestLinkResponse;
import com.project.DriveDesk.DTO.TestLinkRequest;
import com.project.DriveDesk.Models.PlacementTestLink;
import com.project.DriveDesk.Models.Teacher;
import com.project.DriveDesk.Repository.PlacementTestLinkRepository;
import com.project.DriveDesk.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestLinkService {

    private final TeacherRepository teacherRepository;
    private final PlacementTestLinkRepository testLinkRepository;

    // 🔹 Create test link
    public PlacementTestLinkResponse createTestLink(TestLinkRequest request, String teacherUsername) {
        Teacher teacher = teacherRepository.findByUser_Username(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        PlacementTestLink testLink = new PlacementTestLink();
        testLink.setTitle(request.getTitle());
        testLink.setLink(request.getLink());
        testLink.setDepartment(teacher.getDepartment());
        testLink.setTestDate(request.getTestDate());
        testLink.setDescription(request.getDescription());
        testLink.setPostedBy(teacher);

        return mapToDto(testLinkRepository.save(testLink));
    }

    // 🔹 Update test link
    public PlacementTestLinkResponse updateTestLink(Long linkId, TestLinkRequest request, String teacherUsername) {
        Teacher teacher = teacherRepository.findByUser_Username(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        PlacementTestLink existingLink = testLinkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Test link not found"));

        if (!existingLink.getPostedBy().getId().equals(teacher.getId())) {
            throw new RuntimeException("You are not authorized to update this test link");
        }

        existingLink.setTitle(request.getTitle());
        existingLink.setLink(request.getLink());
        existingLink.setTestDate(request.getTestDate());
        existingLink.setDescription(request.getDescription());
        existingLink.setDepartment(teacher.getDepartment());

        return mapToDto(testLinkRepository.save(existingLink));
    }

    // 🔹 Get test links for students by department (non-paginated)
    public Page<PlacementTestLinkResponse> getTestLinksForStudent(String department, Pageable pageable) {
        return testLinkRepository.findByDepartment(department, pageable)
                .map(this::mapToDto);
    }


    // 🔹 Get test links posted by teacher (paginated)
    public Page<PlacementTestLinkResponse> getTestLinksByTeacher(String teacherUsername, Pageable pageable) {
        Teacher teacher = teacherRepository.findByUser_Username(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return testLinkRepository.findByPostedBy(teacher, pageable)
                .map(this::mapToDto);
    }

    // 🔹 Delete test link
    public void deleteTestLink(Long linkId) {
        if (!testLinkRepository.existsById(linkId)) {
            throw new RuntimeException("Test link not found");
        }
        testLinkRepository.deleteById(linkId);
    }

    // 🔹 Map entity to DTO
    public PlacementTestLinkResponse mapToDto(PlacementTestLink link) {
        PlacementTestLinkResponse dto = new PlacementTestLinkResponse();
        dto.setId(link.getId());
        dto.setTitle(link.getTitle());
        dto.setLink(link.getLink());
        dto.setDepartment(link.getDepartment());
        dto.setTestDate(String.valueOf(link.getTestDate()));
        dto.setDescription(link.getDescription());
        dto.setPostedByUsername(link.getPostedBy().getUser().getUsername());
        dto.setPostedByEmail(link.getPostedBy().getUser().getEmail());
        return dto;
    }
}
