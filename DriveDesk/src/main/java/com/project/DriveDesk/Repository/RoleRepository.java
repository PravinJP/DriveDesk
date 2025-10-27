package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.AppRole;
import com.project.DriveDesk.Models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Roles,Long> {


    Optional<Roles> findByRoleName(AppRole appRole);
    boolean existsByRoleName(AppRole roleName);
}