package com.calendar.util;

import com.calendar.service.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    private static final String REFRESH_COOKIE_NAME = "REFRESH_TOKEN";
    private static final String COOKIE_PATH = "/";

    private final JwtProvider jwtProvider;

    public CookieUtils(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public void writeRefreshCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(COOKIE_PATH);
        int maxAgeSeconds = (int) (jwtProvider.getRefreshTokenExpiration() / 1000);
        cookie.setMaxAge(maxAgeSeconds);
        response.addCookie(cookie);
    }

    public void clearRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}


