package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.PlacementTestLink;
import com.project.DriveDesk.Models.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlacementTestLinkRepository extends JpaRepository<PlacementTestLink, Long> {
    Page<PlacementTestLink> findByDepartment(String department, Pageable pageable);


    Page<PlacementTestLink> findByPostedBy(Teacher teacher, Pageable pageable);


}

