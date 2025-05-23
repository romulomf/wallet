package com.wallet.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class WalletItemDTO {

	private Long id;

	@NotNull(message = "O id da carteira é obrigatório")
	private Long wallet;

	@NotNull(message = "Informe uma data")
	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy", locale = "pt-BR", timezone = "Brazil/East")
	private Date date;

	@NotNull(message = "Informe um tipo")
	@Pattern(regexp = "^(ENTRADA|SAÍDA)$", message = "Para o tipo somente são aceitos os valores ENTRADA ou SAÍDA")
	private String type;

	@NotNull(message = "Informe uma descrição")
	@Length(min = 5, message = "A descrição deve ter no mínimo 5 caracteres")
	private String description;

	@NotNull(message = "Informe um valor")
	private BigDecimal value;
}