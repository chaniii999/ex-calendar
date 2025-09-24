package com.calendar.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenReq {

	@NotBlank
	private String refreshToken;
}