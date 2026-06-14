package com.wallet.service.impl;

import org.springframework.stereotype.Service;

import com.wallet.entity.Wallet;
import com.wallet.repository.WalletRepository;
import com.wallet.service.WalletService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

	private final WalletRepository repository;

	@Override
	public Wallet save(Wallet wallet) {
		return repository.save(wallet);
	}
}