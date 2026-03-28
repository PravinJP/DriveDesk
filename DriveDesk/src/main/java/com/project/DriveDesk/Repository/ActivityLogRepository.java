package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    // Fetch latest N entries by ID in descending order
    @Query(value = "SELECT * FROM activity_log ORDER BY id DESC LIMIT ?1", nativeQuery = true)
    List<ActivityLog> findTopByOrderByIdDesc(int limit);

}
