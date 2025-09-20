package com.calendar.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenReq {
	@Email
	@NotBlank
	private String email;

	@NotBlank
	private String refreshToken;
}