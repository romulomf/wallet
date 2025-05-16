package com.wallet.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.wallet.util.enums.TypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "WALLET_ITEMS")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WalletItem implements Serializable {

	private static final long serialVersionUID = -3346995751398028861L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@JoinColumn(name = "WALLET_ID", referencedColumnName = "ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Wallet wallet;

	@NotNull
	@Column(name = "DATE")
	private Date date;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE")
	private TypeEnum type;

	@NotNull
	@Column(name = "DESCRIPTION")
	private String description;

	@NotNull
	@Column(name = "VALUE")
	private BigDecimal value;
}