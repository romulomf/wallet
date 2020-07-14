package com.wallet.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.wallet.util.enums.TypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "WALLET_ITEMS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletItem implements Serializable {

	private static final long serialVersionUID = -3346995751398028861L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@JoinColumn(name = "WALLET", referencedColumnName = "ID")
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