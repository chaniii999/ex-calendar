package com.calendar.dto.auth;

import lombok.Data;

@Data
public class TokenRes {
    private final String accessToken;
    private final String refreshToken;

}
