package com.calendar.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRes {
    private final String accessToken;
    private final String refreshToken;

}
