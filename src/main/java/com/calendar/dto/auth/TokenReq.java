package com.calendar.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class TokenReq {
    private String email;
    private  String refreshToken;
}