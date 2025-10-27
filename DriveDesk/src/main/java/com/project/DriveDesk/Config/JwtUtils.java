package com.project.DriveDesk.Config;

import com.project.DriveDesk.Models.Users;
import com.project.DriveDesk.Models.Teacher;
import com.project.DriveDesk.Models.Student;
import com.project.DriveDesk.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.app.jwtCookieName}")
    private String jwtCookie;

    // 🔹 Extract JWT token from Cookie
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        return cookie != null ? cookie.getValue() : null;
    }

    // 🔹 Extract JWT token from Authorization header (Bearer Token)
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // =====================================================================
    // ✅ Generate Tokens
    // =====================================================================

    // 🔹 For Admin (using UserDetailsImpl)
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateToken(userPrincipal.getUsername(), userPrincipal.getAuthority().getAuthority());
        return buildCookie(jwt);
    }

    // For Admin
    public String generateTokenForAdmin(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }


    // For Teacher
    public String generateTokenForTeacher(Teacher teacher) {
        return generateToken(teacher.getUser().getUsername(), "ROLE_TEACHER");
    }

    // For Student
    public String generateTokenForStudent(Student student) {
        return generateToken(student.getUser().getUsername(), "ROLE_STUDENT");
    }



    // Common generator
    private String generateToken(String subject, String role) {
        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    // 🔹 Helper: Build secure cookie
    private ResponseCookie buildCookie(String jwt) {
        return ResponseCookie.from(jwtCookie, jwt)
                .path("/")
                .maxAge(jwtExpirationMs / 1000)
                .httpOnly(true)
                .secure(false)        // ⚠️ set true in prod if HTTPS
                .build();
    }

    // 🔹 Invalidate Token (Logout)
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
    }

    // =====================================================================
    // ✅ Token Parsing
    // =====================================================================

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getRoleFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }

    // 🔹 Generate signing key
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 🔹 Validate JWT
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}