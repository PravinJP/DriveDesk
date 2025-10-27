package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.DriveAnnouncement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriveAnnouncementRepository extends JpaRepository<DriveAnnouncement, Long> {
    List<DriveAnnouncement> findByDepartment(String department);
    Page<DriveAnnouncement> findByDepartment(String department, Pageable pageable);
}
