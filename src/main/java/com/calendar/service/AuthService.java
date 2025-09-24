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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * 로그인 처리: 이메일, 패스워드 검증 후 Access/Refresh 토큰 발급
     */
    @Transactional
    public TokenRes login(String email, String rawPassword) {
        log.info("auth.login.attempt email={}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            log.warn("auth.login.failed.invalid_password email={}", email);
            throw new AuthenticationFailedException("Invalid credentials");
        }

        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken(email);

        // Redis에 Refresh Token 해시 저장
        refreshTokenRepository.save(email, sha256Hex(refreshToken), jwtProvider.getRefreshTokenExpiration());
        log.info("auth.login.success email={}", email);

        return new TokenRes(accessToken, refreshToken);
    }

    /**
     * RefreshToken 검증 + 회전 후 AccessToken 재발급
     */
    @Transactional
    public TokenRes reissue(String refreshToken) {
        log.info("auth.reissue.attempt");
        // 1) 토큰 유효성/서명/만료 검증
        if (!jwtProvider.isTokenValid(refreshToken)) {
            log.warn("auth.reissue.failed.invalid_token.reason=jwt_invalid");
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        // 2) 토큰 subject(email) 추출
        String email = jwtProvider.getSubject(refreshToken);

        // 3) 저장소의 토큰 해시와 일치 여부 확인 (도난/재사용 방지)
        String savedTokenHash = refreshTokenRepository.findByKey(email);
        String providedTokenHash = sha256Hex(refreshToken);
        if (savedTokenHash == null || !savedTokenHash.equals(providedTokenHash)) {
            log.warn("auth.reissue.failed.invalid_token.reason=hash_mismatch email={}", email);
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        // 4) AccessToken 재발급 + RefreshToken 회전
        String newAccessToken = jwtProvider.createAccessToken(email);
        String newRefreshToken = jwtProvider.createRefreshToken(email);

        // 5) 저장소 갱신 (회전 적용: 기존 토큰 치환) - 해시 저장
        refreshTokenRepository.save(email, sha256Hex(newRefreshToken), jwtProvider.getRefreshTokenExpiration());
        log.info("auth.reissue.success email={}", email);

        return new TokenRes(newAccessToken, newRefreshToken);
    }

    /**
     * 로그아웃 처리 (Redis에서 RefreshToken 삭제)
     */
    @Transactional
    public void logout(String email) {
        refreshTokenRepository.delete(email);
        log.info("auth.logout.success email={}", email);
    }

    @Transactional
    public void signup(SignUpReq req) {
        log.info("auth.signup.attempt email={}", req.getEmail());
        if (userRepository.existsByEmail(req.getEmail())) {
            log.warn("auth.signup.failed.email_conflict email={}", req.getEmail());
            throw new ConflictException("Email already in use");
        }

        User user = userMapper.toEntity(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
        log.info("auth.signup.success email={}", user.getEmail());
    }

    private String sha256Hex(String value) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash refresh token", e);
        }
    }
}
