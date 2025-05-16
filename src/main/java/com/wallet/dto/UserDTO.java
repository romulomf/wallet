package com.wallet.dto;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class UserDTO {

	private Long id;

	@Length(min = 3, max = 50, message = "O nome deve conter entre 3 e 50 caracteres")
	private String name;

	@NotNull
	@Length(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
	private String password;

	@Email(message = "E-mail inválido")
	private String email;

	@NotNull(message = "Informe um perfil de acesso")
	@Pattern(regexp = "^(ROLE_ADMIN|ROLE_USER)$", message = "Para o perfil de acesso somente são aceitos os valores ROLE_ADMIN ou ROLE_USER")
	private String role;
}