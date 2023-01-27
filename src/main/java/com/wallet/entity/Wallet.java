package com.wallet.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "WALLETS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet implements Serializable {

	private static final long serialVersionUID = -6079769300175684582L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "VALUE", nullable = false)
	private BigDecimal value;

	public Wallet(String name, BigDecimal value) {
		this();
		this.name = name;
		this.value = value;
	}
}