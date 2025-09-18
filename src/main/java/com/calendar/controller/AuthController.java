package com.calendar.controller;

import com.calendar.dto.auth.LoginReq;
import com.calendar.dto.auth.LogoutReq;
import com.calendar.dto.auth.TokenReq;
import com.calendar.entity.User;
import com.calendar.repository.UserRepository;
import com.calendar.service.AuthService;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return ResponseEntity.ok(authService.login(user.getEmail(), request.getPassword()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody TokenReq request) {
        return ResponseEntity.ok(authService.reissue(request.getEmail(), request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutReq request) {
        authService.logout(request.getEmail());
        return ResponseEntity.ok("Logged out successfully");
    }
}
