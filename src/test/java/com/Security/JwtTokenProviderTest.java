package com.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWT Token Provider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String TEST_SECRET = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkw";
    private static final long TEST_EXPIRATION = 900000L;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", TEST_EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpirationMs", 604800000L);
    }

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {

        @Test
        @DisplayName("Should generate token from authentication")
        void shouldGenerateTokenFromAuthentication() {
            UserDetails userDetails = User.builder()
                    .username("testuser")
                    .password("password")
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .build();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String token = jwtTokenProvider.generateToken(authentication);

            assertNotNull(token);
            assertFalse(token.isEmpty());
        }

        @Test
        @DisplayName("Should generate token from username and roles")
        void shouldGenerateTokenFromUsernameAndRoles() {
            String token = jwtTokenProvider.generateTokenFromUsername("testuser", "ROLE_USER,ROLE_ADMIN");

            assertNotNull(token);
            assertFalse(token.isEmpty());
        }

        @Test
        @DisplayName("Should generate different tokens for different users")
        void shouldGenerateDifferentTokens() {
            String token1 = jwtTokenProvider.generateTokenFromUsername("user1", "ROLE_USER");
            String token2 = jwtTokenProvider.generateTokenFromUsername("user2", "ROLE_USER");

            assertNotEquals(token1, token2);
        }
    }

    @Nested
    @DisplayName("Token Extraction Tests")
    class TokenExtractionTests {

        @Test
        @DisplayName("Should extract username from token")
        void shouldExtractUsernameFromToken() {
            String token = jwtTokenProvider.generateTokenFromUsername("testuser", "ROLE_USER");

            String username = jwtTokenProvider.getUsernameFromToken(token);

            assertEquals("testuser", username);
        }

        @Test
        @DisplayName("Should extract roles from token")
        void shouldExtractRolesFromToken() {
            String token = jwtTokenProvider.generateTokenFromUsername("testuser", "ROLE_USER,ROLE_ADMIN");

            String roles = jwtTokenProvider.getRolesFromToken(token);

            assertEquals("ROLE_USER,ROLE_ADMIN", roles);
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate valid token")
        void shouldValidateValidToken() {
            String token = jwtTokenProvider.generateTokenFromUsername("testuser", "ROLE_USER");

            assertTrue(jwtTokenProvider.validateToken(token));
        }

        @Test
        @DisplayName("Should invalidate malformed token")
        void shouldInvalidateMalformedToken() {
            assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
        }

        @Test
        @DisplayName("Should invalidate empty token")
        void shouldInvalidateEmptyToken() {
            assertFalse(jwtTokenProvider.validateToken(""));
        }

        @Test
        @DisplayName("Should invalidate tampered token")
        void shouldInvalidateTamperedToken() {
            String token = jwtTokenProvider.generateTokenFromUsername("testuser", "ROLE_USER");
            String tamperedToken = token.substring(0, token.length() - 5) + "xxxxx";

            assertFalse(jwtTokenProvider.validateToken(tamperedToken));
        }
    }

    @Nested
    @DisplayName("Expiration Configuration Tests")
    class ExpirationTests {

        @Test
        @DisplayName("Should return correct JWT expiration")
        void shouldReturnCorrectJwtExpiration() {
            assertEquals(TEST_EXPIRATION, jwtTokenProvider.getJwtExpirationMs());
        }

        @Test
        @DisplayName("Should return correct refresh expiration")
        void shouldReturnCorrectRefreshExpiration() {
            assertEquals(604800000L, jwtTokenProvider.getRefreshExpirationMs());
        }
    }
}
