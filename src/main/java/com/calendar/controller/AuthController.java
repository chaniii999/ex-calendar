package com.calendar.controller;

import com.calendar.dto.ApiResponse;
import com.calendar.dto.auth.LoginReq;
import com.calendar.dto.auth.LogoutReq;
import com.calendar.dto.auth.SignUpReq;
import com.calendar.dto.auth.TokenReq;
import com.calendar.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpReq request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.of(true, "User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq request) {
        return ResponseEntity.ok(ApiResponse.of(true, "ok", authService.login(request.getEmail(), request.getPassword())));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@Valid @RequestBody TokenReq request) {
        return ResponseEntity.ok(ApiResponse.of(true, "ok", authService.reissue(request.getEmail(), request.getRefreshToken())));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutReq request) {
        authService.logout(request.getEmail());
        return ResponseEntity.ok(ApiResponse.of(true, "Logged out successfully", null));
    }
}
