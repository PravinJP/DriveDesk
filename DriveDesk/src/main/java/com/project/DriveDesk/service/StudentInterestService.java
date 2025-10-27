package com.project.DriveDesk.service;

import com.project.DriveDesk.DTO.StudentInterestRequest;
import com.project.DriveDesk.DTO.StudentInterestResponse;
import com.project.DriveDesk.Models.JobDescription;
import com.project.DriveDesk.Models.Student;
import com.project.DriveDesk.Models.StudentInterest;
import com.project.DriveDesk.Repository.JDRepository;
import com.project.DriveDesk.Repository.StudentInterestRepository;
import com.project.DriveDesk.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentInterestService {

    private final StudentRepository studentRepository;
    private final JDRepository jdRepository;
    private final StudentInterestRepository interestRepository;

    public StudentInterestResponse registerInterest(String studentUsername, StudentInterestRequest request) {
        Student student = studentRepository.findByUser_Username(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        JobDescription jd = jdRepository.findById(request.getJdId())
                .orElseThrow(() -> new RuntimeException("JD not found"));

        if (interestRepository.existsByStudentAndJobDescription(student, jd)) {
            throw new RuntimeException("Already registered interest");
        }

        StudentInterest interest = new StudentInterest();
        interest.setStudent(student);
        interest.setJobDescription(jd);
        interest.setResumeUrl(request.getResumeUrl());

        StudentInterest saved = interestRepository.save(interest);
        return mapToDto(saved);
    }

    public List<StudentInterestResponse> getInterestedStudents(Long jdId) {
        JobDescription jd = jdRepository.findById(jdId)
                .orElseThrow(() -> new RuntimeException("JD not found"));
        return interestRepository.findByJobDescription(jd)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<StudentInterestResponse> getMyInterests(String studentUsername) {
        Student student = studentRepository.findByUser_Username(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return interestRepository.findByStudent(student)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private StudentInterestResponse mapToDto(StudentInterest interest) {
        StudentInterestResponse dto = new StudentInterestResponse();
        dto.setId(interest.getId());
        dto.setJdId(interest.getJobDescription().getId());


        Student student = interest.getStudent();
        dto.setRollNumber(student.getRollNumber());
        dto.setDepartment(student.getDepartment());
        dto.setStudentUsername(student.getUser().getUsername());
        dto.setStudentEmail(student.getUser().getEmail());

        return dto;
    }
}
