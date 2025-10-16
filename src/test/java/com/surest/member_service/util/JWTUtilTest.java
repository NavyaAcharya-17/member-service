package com.surest.member_service.util;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JWTUtilTest {

    private JWTUtil jwtUtil;
    private final String SECRET = "mysecretmysecretmysecretmysecret";
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "EXPIRATION_TIME", EXPIRATION_TIME);
        jwtUtil.init();
    }

    @Test
    void testGenerateTokenNotNull() {
        String token = jwtUtil.generateToken("testUser");
        assertNotNull(token, "Generated token should not be null");
    }

    @Test
    void testExtractUsername() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername, "Extracted username should match the original");
    }

    @Test
    void testValidateToken_Success() {
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        String token = jwtUtil.generateToken(username);
        assertTrue(jwtUtil.validateToken(username, userDetails, token), "Token should be valid");
    }

    @Test
    void testValidateToken_Failure_WrongUsername() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("otherUser");
        String token = jwtUtil.generateToken("testUser");
        assertFalse(jwtUtil.validateToken("testUser", userDetails, token), "Token should be invalid for wrong username");
    }

    @Test
    void testValidateToken_Failure_ExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtil, "EXPIRATION_TIME", 1000L); // 1 second
        jwtUtil.init();
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        String token = jwtUtil.generateToken(username);
        Thread.sleep(1100L);
        assertFalse(jwtUtil.validateToken(username, userDetails, token));
    }

    @Test
    void testTokenExpirationCheck_ExpiredJwtException() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtil, "EXPIRATION_TIME", 100L);
        jwtUtil.init();
        String token = jwtUtil.generateToken("testUser");
        Thread.sleep(200L);
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.extractUsername(token);
        });
    }

    @Test
    void testGenerateToken_ContainsUsername() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);
        assertTrue(token.contains("."), "JWT token should contain dots separating header, payload, signature");
    }
}
