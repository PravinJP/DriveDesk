package com.project.DriveDesk.service;

import com.project.DriveDesk.DTO.JDRequest;
import com.project.DriveDesk.DTO.JobDescriptionResponse;
import com.project.DriveDesk.Models.JobDescription;
import com.project.DriveDesk.Models.Teacher;
import com.project.DriveDesk.Repository.JDRepository;
import com.project.DriveDesk.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JDService {

    private final JDRepository jdRepository;
    private final TeacherRepository teacherRepository;

    // 🔹 Create JD and return DTO
    public JobDescriptionResponse createJD(JDRequest jdRequest) {
        String teacherUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByUser_Username(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        JobDescription jd = new JobDescription();
        jd.setCompanyName(jdRequest.getCompanyName());
        jd.setRole(jdRequest.getRole());
        jd.setEligibilityBranch(jdRequest.getEligibilityBranch());
        jd.setEligibilityCgpa(jdRequest.getEligibilityCgpa());
        jd.setDeadline(jdRequest.getDeadline());
        jd.setDescription(jdRequest.getDescription());
        jd.setDepartment(teacher.getDepartment());
        jd.setPostedBy(teacher);
        jd.setJdPdfUrl(jdRequest.getJdPdfUrl());

        JobDescription savedJD = jdRepository.save(jd);
        return mapToDto(savedJD);
    }

    // 🔹 Get all JDs as DTOs
    public Page<JobDescriptionResponse> getAllJDs(Pageable pageable) {
        return jdRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    // 🔹 Update JD and return DTO
    public JobDescriptionResponse updateJD(Long jdId, JDRequest request) {
        JobDescription jd = jdRepository.findById(jdId)
                .orElseThrow(() -> new RuntimeException("JD not found"));

        jd.setCompanyName(request.getCompanyName());
        jd.setRole(request.getRole());
        jd.setEligibilityBranch(request.getEligibilityBranch());
        jd.setEligibilityCgpa(request.getEligibilityCgpa());
        jd.setDeadline(request.getDeadline());
        jd.setDescription(request.getDescription());
        jd.setJdPdfUrl(request.getJdPdfUrl());

        JobDescription updatedJD = jdRepository.save(jd);
        return mapToDto(updatedJD);
    }

    // 🔹 Delete JD
    public void deleteJD(Long jdId) {
        if (!jdRepository.existsById(jdId)) {
            throw new RuntimeException("JD not found");
        }
        jdRepository.deleteById(jdId);
    }

    // 🔹 Get JDs by logged-in teacher
    public List<JobDescriptionResponse> getAllJDsByTeacher() {
        String teacherUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Teacher teacher = teacherRepository.findByUser_Username(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return jdRepository.findByPostedBy(teacher).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // 🔹 DTO Mapper
    public JobDescriptionResponse mapToDto(JobDescription jd) {
        JobDescriptionResponse dto = new JobDescriptionResponse();
        dto.setId(jd.getId());
        dto.setCompanyName(jd.getCompanyName());
        dto.setRole(jd.getRole());
        dto.setEligibilityBranch(jd.getEligibilityBranch());
        dto.setEligibilityCgpa(jd.getEligibilityCgpa());
        dto.setDeadline(jd.getDeadline().toString());
        dto.setDescription(jd.getDescription());
        dto.setDepartment(jd.getDepartment());
        dto.setJdPdfUrl(jd.getJdPdfUrl());
        dto.setPostedByUsername(jd.getPostedBy().getUser().getUsername());
        return dto;
    }
}
