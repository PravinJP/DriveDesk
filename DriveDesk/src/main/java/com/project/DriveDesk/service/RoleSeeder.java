package com.project.DriveDesk.service;


import com.project.DriveDesk.Models.AppRole;
import com.project.DriveDesk.Models.Roles;
import com.project.DriveDesk.Repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        if (roleRepository.findByRoleName(AppRole.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Roles(null, AppRole.ROLE_ADMIN, null));
        }
        if (roleRepository.findByRoleName(AppRole.ROLE_TEACHER).isEmpty()) {
            roleRepository.save(new Roles(null, AppRole.ROLE_TEACHER, null));
        }
        if (roleRepository.findByRoleName(AppRole.ROLE_STUDENT).isEmpty()) {
            roleRepository.save(new Roles(null, AppRole.ROLE_STUDENT, null));
        }
    }
}