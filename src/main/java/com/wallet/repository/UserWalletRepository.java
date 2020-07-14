package com.wallet.repository;

import java.util.Optional;

import com.wallet.entity.UserWallet;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

	Optional<UserWallet> findByUsersIdAndWalletId(Long user, Long wallet);
}