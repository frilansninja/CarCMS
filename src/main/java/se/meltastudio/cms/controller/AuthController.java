package se.meltastudio.cms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.dto.NewPasswordRequest;
import se.meltastudio.cms.dto.PasswordResetRequest;
import se.meltastudio.cms.dto.PasswordResetResponse;
import se.meltastudio.cms.model.Role;
import se.meltastudio.cms.security.JwtUtil;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.repository.UserRepository;
import se.meltastudio.cms.service.PasswordResetService;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordResetService passwordResetService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long companyId = user.getCompany().getId();
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        System.out.println("User found, generating tokens...");
        System.out.println("AccessToken= " + accessToken);
        System.out.println("RefreshToken = " + refreshToken);

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());


        //JwtResponse jwtResponse = new JwtResponse(accessToken, refreshToken, customerId, roles);
        //return ResponseEntity.ok(jwtResponse);
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "companyId", user.getCompany().getId(),
                "roles", roles
        ));
    }

    /**
     * Request password reset - generates token and returns reset link
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequest request) {
        try {
            String rawToken = passwordResetService.createPasswordResetToken(request.getUsername());

            // Build reset link (frontend URL)
            String resetLink = "http://localhost:5173/reset-password/" + rawToken;

            return ResponseEntity.ok(new PasswordResetResponse(
                    resetLink,
                    "Password reset link generated. Please copy and share manually."
            ));
        } catch (Exception e) {
            // Generic message for security (don't reveal if user exists)
            return ResponseEntity.ok(new PasswordResetResponse(
                    null,
                    "If a user with this email exists, a reset link has been generated."
            ));
        }
    }

    /**
     * Reset password with token
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody NewPasswordRequest request) {
        try {
            // Validate password strength (optional but recommended)
            if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Password must be at least 8 characters long"
                ));
            }

            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

            return ResponseEntity.ok(Map.of(
                    "message", "Password has been reset successfully. You can now log in."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid or expired reset token"
            ));
        }
    }
}

// DTO för login request
class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}


// DTO för login response
class JwtResponse {
    private final String accessToken;
    private final String refreshToken;

    private Long companyId;
    private Set<Role> roles;

    public JwtResponse(String accessToken, String refreshToken, Long companyId, Set<Role> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.companyId = companyId;
        this.roles = roles;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }

    public Long getCompanyId ()   { return companyId;}
}

// DTO för refresh-token request
class RefreshRequest {
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}