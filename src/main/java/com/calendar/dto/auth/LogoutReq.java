package com.calendar.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutReq {
	@Email
	@NotBlank
	private String email;
}
