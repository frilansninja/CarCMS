package se.meltastudio.cms.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.meltastudio.cms.model.PasswordResetToken;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.repository.PasswordResetTokenRepository;
import se.meltastudio.cms.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Token expiration: 1 hour
    private static final long TOKEN_EXPIRATION_HOURS = 1;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generate password reset token for a user
     * Returns the raw token (to be displayed to admin/user)
     */
    @Transactional
    public String createPasswordResetToken(String username) {
        // Find user by username (email)
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            // Security: Don't reveal if user exists
            throw new RuntimeException("If a user with this email exists, a reset link has been generated.");
        }

        User user = userOpt.get();

        // Invalidate any existing active tokens for this user
        tokenRepository.deleteAllByUser(user);

        // Generate cryptographically secure random token
        String rawToken = UUID.randomUUID().toString();

        // Hash the token before storing (like password hashing)
        String tokenHash = passwordEncoder.encode(rawToken);

        // Create and save token entity
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(tokenHash);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS));
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        // Return raw token (only time it's available in plaintext)
        return rawToken;
    }

    /**
     * Validate token and reset password
     */
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        // Find all tokens and check which one matches (since we hash tokens)
        // This is necessary because we can't reverse the hash
        PasswordResetToken validToken = tokenRepository.findAll().stream()
                .filter(token -> !token.isUsed() && !token.isExpired())
                .filter(token -> passwordEncoder.matches(rawToken, token.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        // Validate token status
        if (!validToken.isValid()) {
            throw new RuntimeException("Token has expired or already been used");
        }

        // Update user password
        User user = validToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        validToken.setUsed(true);
        tokenRepository.save(validToken);

        // Optional: Delete all tokens for this user for extra security
        tokenRepository.deleteAllByUser(user);
    }

    /**
     * Cleanup expired tokens (can be called by scheduled job)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
