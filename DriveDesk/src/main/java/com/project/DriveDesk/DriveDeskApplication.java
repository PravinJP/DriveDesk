package com.project.DriveDesk;

import com.project.DriveDesk.Models.AppRole;
import com.project.DriveDesk.Models.Users;
import com.project.DriveDesk.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DriveDeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriveDeskApplication.class, args);
	}


	@Bean
	CommandLineRunner initDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (!userRepository.existsByRole(AppRole.ROLE_ADMIN)) {
				Users admin = Users.builder()
						.username("default_admin")
						.email("admin@example.com")
						.password(passwordEncoder.encode("admin123"))
						.role(AppRole.ROLE_ADMIN)
						.build();

				userRepository.save(admin);
				System.out.println("✅ Default admin created: admin@example.com / admin123");
			}
		};
	}

	// ✅ RestTemplate bean for GenAI calls
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
