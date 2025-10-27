package com.project.DriveDesk.service;

import com.project.DriveDesk.Models.JobDescription;
import com.project.DriveDesk.Models.PlacementTestLink;
import com.project.DriveDesk.Models.Student;
import com.project.DriveDesk.Repository.JDRepository;
import com.project.DriveDesk.Repository.PlacementTestLinkRepository;
import com.project.DriveDesk.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentDashboardService {

    private final StudentRepository studentRepository;
    private final JDRepository jdRepository;
    private final PlacementTestLinkRepository testLinkRepository;

    public List<JobDescription> getAllJDsForStudent(String studentUsername) {
        Student student = studentRepository.findByUser_Username(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return jdRepository.findByDepartment(student.getDepartment());
    }

    public Page<PlacementTestLink> getAllTestLinksForStudent(String studentUsername, Pageable pageable) {
        Student student = studentRepository.findByUser_Username(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return testLinkRepository.findByDepartment(student.getDepartment(), pageable);
    }

}
