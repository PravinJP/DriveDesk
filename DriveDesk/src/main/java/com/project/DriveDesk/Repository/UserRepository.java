package com.project.DriveDesk.Repository;

import com.project.DriveDesk.Models.AppRole;
import com.project.DriveDesk.Models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.relation.Role;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long> {

    boolean existsByRole(AppRole role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Users> findByUsername(String username);
}
