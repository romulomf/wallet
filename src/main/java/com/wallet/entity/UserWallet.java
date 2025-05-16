package com.wallet.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "USERS_WALLETS")
@Data
public class UserWallet implements Serializable {

	private static final long serialVersionUID = -6827119066231685552L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private User users;

	@JoinColumn(name = "WALLET_ID", referencedColumnName = "ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Wallet wallet;
}