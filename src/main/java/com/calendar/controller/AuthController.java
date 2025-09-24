package com.calendar.controller;

import com.calendar.dto.ApiResponse;
import com.calendar.dto.auth.*;
import com.calendar.service.AuthService;
import com.calendar.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final CookieUtils cookieUtils;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignUpReq request) {
        authService.signup(request);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.of(true, "User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenRes>> login(@Valid @RequestBody LoginReq request) {
        return ResponseEntity.ok(ApiResponse.of(true, "ok", authService.login(request.getEmail(), request.getPassword())));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenRes>> reissue(jakarta.servlet.http.HttpServletRequest request,
                                                         jakarta.servlet.http.HttpServletResponse response) {
        String refreshToken = cookieUtils.resolveRefreshToken(request);
        TokenRes tokens = authService.reissue(refreshToken);
        cookieUtils.writeRefreshCookie(response, tokens.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.of(true, "ok", tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication,
                                                    jakarta.servlet.http.HttpServletResponse response) {
        authService.logout(authentication.getName());
        cookieUtils.clearRefreshCookie(response);
        return ResponseEntity.ok(ApiResponse.of(true, "Logged out successfully", null));
    }
}
