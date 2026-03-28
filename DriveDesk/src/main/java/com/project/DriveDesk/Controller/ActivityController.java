package com.project.DriveDesk.Controller;

import com.project.DriveDesk.DTO.ActivityDto;
import com.project.DriveDesk.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * Endpoint: /api/users/activity/latest/{limit}
     * Returns the N most recent activity logs (by creation time)
     */
    @GetMapping("/activity/latest/{limit}")
    public ResponseEntity<List<ActivityDto>> getRecentActivity(@PathVariable Integer limit) {
        List<ActivityDto> activities = activityService.getRecentActivity(limit);
        return ResponseEntity.ok(activities);
    }
}
