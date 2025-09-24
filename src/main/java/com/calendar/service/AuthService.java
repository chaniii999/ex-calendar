package com.calendar.service;

import com.calendar.dto.auth.SignUpReq;
import com.calendar.dto.auth.TokenRes;
import com.calendar.exception.AuthenticationFailedException;
import com.calendar.exception.ConflictException;
import com.calendar.mapper.UserMapper;
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
    private final UserMapper userMapper;

    /**
     * 로그인 처리: 이메일, 패스워드 검증 후 Access/Refresh 토큰 발급
     */
    public TokenRes login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken(email);

        // Redis에 Refresh Token 저장
        refreshTokenRepository.save(email, refreshToken, jwtProvider.getRefreshTokenExpiration());

        return new TokenRes(accessToken, refreshToken);
    }

    /**
     * RefreshToken 검증 + 회전 후 AccessToken 재발급
     */
    public TokenRes reissue(String refreshToken) {
        // 1) 토큰 유효성/서명/만료 검증
        if (!jwtProvider.isTokenValid(refreshToken)) {
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        // 2) 토큰 subject(email) 추출
        String email = jwtProvider.getSubject(refreshToken);

        // 3) 저장소의 토큰과 일치 여부 확인 (도난/재사용 방지)
        String savedToken = refreshTokenRepository.findByKey(email);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        // 4) AccessToken 재발급 + RefreshToken 회전
        String newAccessToken = jwtProvider.createAccessToken(email);
        String newRefreshToken = jwtProvider.createRefreshToken(email);

        // 5) 저장소 갱신 (회전 적용: 기존 토큰 치환)
        refreshTokenRepository.save(email, newRefreshToken, jwtProvider.getRefreshTokenExpiration());

        return new TokenRes(newAccessToken, newRefreshToken);
    }

    /**
     * 로그아웃 처리 (Redis에서 RefreshToken 삭제)
     */
    public void logout(String email) {
        refreshTokenRepository.delete(email);
    }

    public void signup(SignUpReq req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        User user = userMapper.toEntity(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
    }
}
