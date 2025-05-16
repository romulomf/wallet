package com.wallet.security.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JwtAuthenticationDTO {

	@NotNull(message = "Informe um e-mail")
	private String email;
	
	@NotNull(message = "Informe uma senha")
	private String password;
}