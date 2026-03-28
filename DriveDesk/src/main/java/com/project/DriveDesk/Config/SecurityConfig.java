package com.project.DriveDesk.Config;

import com.project.DriveDesk.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ FIXED CORS (IMPORTANT)
                .cors(cors -> {})

                // Disable CSRF for APIs
                .csrf(csrf -> csrf.disable())

                // Exception handling
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))

                // Stateless JWT session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Allow error page
                        .requestMatchers("/error").permitAll()

                        // Admin signup
                        .requestMatchers("/api/auth/admin-signup").permitAll()

                        // Allow OPTIONS for CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public login endpoints
                        .requestMatchers("/api/auth/admin/login").permitAll()
                        .requestMatchers("/api/auth/teacher/login").permitAll()
                        .requestMatchers("/api/auth/student/login").permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/register").hasRole("ADMIN")

                        // JD endpoints
                        .requestMatchers("/api/jd/create").hasRole("TEACHER")
                        .requestMatchers("/api/jd/update/**").hasRole("TEACHER")
                        .requestMatchers("/api/jd/delete/**").hasRole("TEACHER")
                        .requestMatchers("/api/jd/teacher").hasRole("TEACHER")

                        .requestMatchers(HttpMethod.GET, "/api/jd/student")
                        .hasAnyRole("STUDENT", "TEACHER", "ADMIN")

                        // Questions
                        .requestMatchers(HttpMethod.POST, "/api/questions").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/questions").permitAll()

                        // Interest
                        .requestMatchers("/api/interest/register").hasRole("STUDENT")
                        .requestMatchers("/api/interest/student").hasRole("STUDENT")
                        .requestMatchers("/api/interest/jd/**")
                        .hasAnyRole("STUDENT", "TEACHER", "ADMIN")

                        // Tests
                        .requestMatchers("/api/tests/**")
                        .hasAnyRole("TEACHER", "STUDENT")

                        // Authenticated endpoints
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers("/api/auth/me").authenticated()

                        // View tests
                        .requestMatchers(HttpMethod.GET, "/api/test/all")
                        .hasAnyRole("STUDENT", "TEACHER", "ADMIN")

                        // Everything else
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}