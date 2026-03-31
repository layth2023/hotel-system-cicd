package com.Security;

import com.User.User;
import com.User.UserNotFoundException;
import com.User.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing password reset tokens.
 * Handles token creation, validation, and consumption.
 */
@Service
@Transactional
public class PasswordResetTokenService {

    @Value("${app.security.password-reset-expiration:3600000}")
    private long resetExpirationMs; // 1 hour default

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public PasswordResetTokenService(PasswordResetTokenRepository tokenRepository,
                                     UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a password reset token for a user by email
     * Returns null if user not found (security: don't reveal if email exists)
     */
    public PasswordResetToken createResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return null; // Don't reveal if email exists
        }

        User user = userOpt.get();

        // Invalidate any existing tokens for this user
        tokenRepository.invalidateAllUserTokens(user);

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(resetExpirationMs));
        token.setUsed(false);

        return tokenRepository.save(token);
    }

    /**
     * Find a token by its string value
     */
    @Transactional(readOnly = true)
    public Optional<PasswordResetToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    /**
     * Validate and consume a token (mark as used)
     * Returns the user associated with the token
     * Throws exception if token is invalid
     */
    public User validateAndConsumeToken(String tokenString) {
        PasswordResetToken token = tokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new PasswordResetTokenException("Invalid password reset token"));

        if (token.isExpired()) {
            tokenRepository.delete(token);
            throw new PasswordResetTokenException("Password reset token has expired");
        }

        if (token.isUsed()) {
            throw new PasswordResetTokenException("Password reset token has already been used");
        }

        // Mark token as used
        token.setUsed(true);
        tokenRepository.save(token);

        return token.getUser();
    }

    /**
     * Delete all tokens for a user
     */
    public void deleteAllUserTokens(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        tokenRepository.deleteAllByUser(user);
    }

    /**
     * Delete all expired tokens (can be scheduled)
     */
    public void deleteExpiredTokens() {
        tokenRepository.deleteExpiredTokens(Instant.now());
    }
}
