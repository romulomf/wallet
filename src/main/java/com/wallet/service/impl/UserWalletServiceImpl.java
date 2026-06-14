package com.wallet.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wallet.entity.UserWallet;
import com.wallet.repository.UserWalletRepository;
import com.wallet.service.UserWalletService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserWalletServiceImpl implements UserWalletService {

	private final UserWalletRepository repository;

	@Override
	public UserWallet save(UserWallet userWallet) {
		return repository.save(userWallet);
	}

	@Override
	public Optional<UserWallet> findByUsersIdAndWalletId(Long user, Long wallet) {
		return repository.findByUsersIdAndWalletId(user, wallet);
	}
}