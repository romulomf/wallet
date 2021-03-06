package com.wallet.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "USERS_WALLET")
@Data
public class UserWallet implements Serializable {

	private static final long serialVersionUID = -6827119066231685552L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "USERS", referencedColumnName = "ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private User users;

	@JoinColumn(name = "WALLET", referencedColumnName = "ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Wallet wallet;
}