package com.calendar.service;

import com.calendar.dto.auth.TokenRes;
import com.calendar.repository.RefreshTokenRepository;
import com.calendar.repository.UserRepository;
import com.calendar.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 처리: 이메일, 패스워드 검증 후 Access/Refresh 토큰 발급
     */
    public TokenRes login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken(email);

        // Redis에 Refresh Token 저장
        refreshTokenRepository.save(email, refreshToken, jwtProvider.getRefreshTokenExpiration());

        return new TokenRes(accessToken, refreshToken);
    }

    /**
     * RefreshToken 검증 후 AccessToken 재발급
     */
    public TokenRes reissue(String email, String refreshToken) {
        String savedToken = refreshTokenRepository.findByKey(email);

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtProvider.createAccessToken(email);
        return new TokenRes(newAccessToken, refreshToken);
    }

    /**
     * 로그아웃 처리 (Redis에서 RefreshToken 삭제)
     */
    public void logout(String email) {
        refreshTokenRepository.delete(email);
    }
}
