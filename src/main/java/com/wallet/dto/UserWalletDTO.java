package com.wallet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserWalletDTO {

	private Long id;

	@NotNull(message = "Informe o id do usuário")
	private Long users;

	@NotNull(message = "Informa o id da carteira")
	private Long wallet;
}