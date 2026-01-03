package se.meltastudio.cms.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/util")
@CrossOrigin(origins = "*")
public class PasswordUtilController {

    private final PasswordEncoder passwordEncoder;

    @Value("${util.password.secret:CHANGE_THIS_SECRET}")
    private String utilSecret;

    public PasswordUtilController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/hash-password")
    public ResponseEntity<?> hashPassword(
            @RequestParam String password,
            @RequestParam(required = false) String secret,
            HttpServletRequest request) {

        // Security check 1: Only allow from localhost
        String remoteAddr = request.getRemoteAddr();
        if (!isLocalhost(remoteAddr)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "This endpoint is only accessible from localhost"));
        }

        // Security check 2: Require secret parameter
        if (secret == null || !secret.equals(utilSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid or missing secret parameter"));
        }

        String hashedPassword = passwordEncoder.encode(password);

        return ResponseEntity.ok(Map.of(
            "plainPassword", password,
            "hashedPassword", hashedPassword,
            "sql", "UPDATE users SET password = '" + hashedPassword + "' WHERE username = 'admin';"
        ));
    }

    private boolean isLocalhost(String remoteAddr) {
        return remoteAddr.equals("127.0.0.1") ||
               remoteAddr.equals("0:0:0:0:0:0:0:1") ||
               remoteAddr.equals("::1") ||
               remoteAddr.equals("localhost");
    }
}
