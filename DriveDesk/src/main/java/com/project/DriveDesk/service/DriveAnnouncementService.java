package com.project.DriveDesk.service;

import com.project.DriveDesk.DTO.DriveAnnouncementRequest;
import com.project.DriveDesk.DTO.DriveAnnouncementResponse;
import com.project.DriveDesk.Models.DriveAnnouncement;
import com.project.DriveDesk.Models.Teacher;
import com.project.DriveDesk.Repository.DriveAnnouncementRepository;
import com.project.DriveDesk.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DriveAnnouncementService {

    private final DriveAnnouncementRepository announcementRepository;
    private final TeacherRepository teacherRepository;

    public DriveAnnouncementResponse createAnnouncement(DriveAnnouncementRequest request, String teacherUsername) {
        Teacher teacher = teacherRepository.findByUser_Username(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        DriveAnnouncement announcement = new DriveAnnouncement();
        announcement.setTitle(request.getTitle());
        announcement.setDescription(request.getDescription());
        announcement.setDriveDate(request.getDriveDate());
        announcement.setDepartment(teacher.getDepartment());
        announcement.setPostedBy(teacher);

        return mapToDto(announcementRepository.save(announcement));
    }

    public Page<DriveAnnouncementResponse> getAnnouncementsForStudent(String department, Pageable pageable) {
        return announcementRepository.findByDepartment(department, pageable)
                .map(this::mapToDto);
    }

    private DriveAnnouncementResponse mapToDto(DriveAnnouncement announcement) {
        DriveAnnouncementResponse dto = new DriveAnnouncementResponse();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setDescription(announcement.getDescription());
        dto.setDepartment(announcement.getDepartment());
        dto.setDriveDate(LocalDate.parse(String.valueOf(announcement.getDriveDate())));
        dto.setPostedByUsername(announcement.getPostedBy().getUser().getUsername());
        dto.setPostedByEmail(announcement.getPostedBy().getUser().getEmail());
        return dto;
    }
}

