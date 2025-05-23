package com.wallet.dto;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WalletDTO {

    private Long id;

    @NotNull(message = "O nome é obrigatório")
    @Length(min = 3, message = "O nome deve conter no mínimo 3 caracteres")
    private String name;

    @NotNull(message = "O valor é obrigatório")
    private BigDecimal value;
}