package com.calendar.service;

import com.calendar.entity.User;
import com.calendar.repository.RefreshTokenRepository;
import com.calendar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private JwtProvider jwtProvider;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private AuthService authService;

    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .email("test@test.com")
                .password("encoded1234")
                .nickname("tester")
                .build();
    }

    @Test
    void 로그인_성공() {
        // given
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("1234", "encoded1234")).thenReturn(true);
        when(jwtProvider.createAccessToken("test@test.com")).thenReturn("access-token");
        when(jwtProvider.createRefreshToken("test@test.com")).thenReturn("refresh-token");
        when(jwtProvider.getRefreshTokenExpiration()).thenReturn(1000L);

        // when
        var response = authService.login("test@test.com", "1234");

        // then
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(refreshTokenRepository, times(1))
                .save(eq("test@test.com"), eq("refresh-token"), anyLong());
    }

    @Test
    void 로그인_실패_잘못된비밀번호() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded1234")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> authService.login("test@test.com", "wrong"));
    }

    @Test
    void 재발급_성공() {
        when(jwtProvider.isTokenValid("refresh-token")).thenReturn(true);
        when(jwtProvider.getSubject("refresh-token")).thenReturn("test@test.com");
        when(refreshTokenRepository.findByKey("test@test.com")).thenReturn("refresh-token");
        when(jwtProvider.createAccessToken("test@test.com")).thenReturn("new-access");
        when(jwtProvider.createRefreshToken("test@test.com")).thenReturn("new-refresh");
        when(jwtProvider.getRefreshTokenExpiration()).thenReturn(1000L);

        var response = authService.reissue("refresh-token");

        assertEquals("new-access", response.getAccessToken());
        assertEquals("new-refresh", response.getRefreshToken());
        verify(refreshTokenRepository, times(1))
                .save(eq("test@test.com"), eq("new-refresh"), anyLong());
    }

    @Test
    void 재발급_실패_리프레시토큰불일치() {
        when(jwtProvider.isTokenValid("wrong-token")).thenReturn(true);
        when(jwtProvider.getSubject("wrong-token")).thenReturn("test@test.com");
        when(refreshTokenRepository.findByKey("test@test.com")).thenReturn("other-token");

        assertThrows(RuntimeException.class,
                () -> authService.reissue("wrong-token"));
    }

    @Test
    void 로그아웃_성공() {
        authService.logout("test@test.com");

        verify(refreshTokenRepository, times(1)).delete("test@test.com");
    }
}
