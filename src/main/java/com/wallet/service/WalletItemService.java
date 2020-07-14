package com.wallet.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.wallet.entity.WalletItem;
import com.wallet.util.enums.TypeEnum;

import org.springframework.data.domain.Page;

public interface WalletItemService {

	WalletItem save(WalletItem walletItem);

	Page<WalletItem> findBetweenDates(Long wallet, Date start, Date end, int page);

	List<WalletItem> findByWalletIdAndType(Long wallet, TypeEnum type);

	BigDecimal sumByWalletId(Long wallet);

	Optional<WalletItem> findById(Long id);

	void deleteById(Long id);
}