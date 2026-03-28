package com.project.DriveDesk.service;

import com.project.DriveDesk.DTO.ActivityDto;
import com.project.DriveDesk.Models.ActivityLog;
import com.project.DriveDesk.Repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void log(String action, String name, String type) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setName(name);
        log.setType(type);
        log.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(log);
    }

    public List<ActivityDto> getRecentActivity(int limit) {
        return activityLogRepository.findTopByOrderByIdDesc(limit)
                .stream()
                .map(log -> new ActivityDto(
                        log.getAction(),
                        log.getName(),
                        log.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        log.getType()
                ))
                .collect(Collectors.toList());
    }
}
