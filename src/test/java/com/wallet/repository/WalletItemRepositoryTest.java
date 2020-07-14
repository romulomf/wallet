package com.wallet.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.util.enums.TypeEnum;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@TestInstance(Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class WalletItemRepositoryTest {

	private static final Date DATE = new Date();

	private static final TypeEnum TYPE = TypeEnum.EN;

	private static final String DESCRIPTION = "Conta de luz";

	private static final BigDecimal VALUE = BigDecimal.valueOf(65);

	private Long savedWalletId = null;

	private Long savedWalletItemId = null;

	@Autowired
	private WalletItemRepository repository;

	@Autowired
	private WalletRepository walletRepository;

	@BeforeEach
	void setUp() {
		Wallet wallet = new Wallet();
		wallet.setName("Carteira Teste");
		wallet.setValue(BigDecimal.valueOf(250));
		walletRepository.save(wallet);
		savedWalletId = wallet.getId();

		WalletItem walletItem = new WalletItem(null, wallet, DATE, TYPE, DESCRIPTION, VALUE);
		repository.save(walletItem);
		savedWalletItemId = walletItem.getId();
	}

	@AfterEach
	void tearDown() {
		repository.deleteAll();
		walletRepository.deleteAll();
	}

	@Test
	void testSave() {
		Wallet wallet = new Wallet();
		wallet.setName("Carteira 1");
		wallet.setValue(BigDecimal.valueOf(500));
		walletRepository.save(wallet);

		WalletItem walletItem = new WalletItem(null, wallet, DATE, TYPE, DESCRIPTION, VALUE);

		WalletItem response = repository.save(walletItem);

		Assertions.assertNotNull(response);
		Assertions.assertEquals(DESCRIPTION, response.getDescription());
		Assertions.assertEquals(TYPE, response.getType());
		Assertions.assertEquals(VALUE, response.getValue());
		Assertions.assertEquals(wallet.getId(), response.getWallet().getId());
	}

	@Test
	void testSaveInvalidWalletItem() {
		WalletItem walletItem = new WalletItem(null, null, DATE, null, DESCRIPTION, null);
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			repository.save(walletItem);
		});
	}

	@Test
	void testUpdate() {
		Optional<WalletItem> walletItemOptional = repository.findById(savedWalletItemId);

		final String description = "Descrição Alterada";

		WalletItem changed = walletItemOptional.get();
		changed.setDescription(description);

		repository.save(changed);

		Optional<WalletItem> newWalletItemOptional = repository.findById(savedWalletItemId);

		Assertions.assertEquals(description, newWalletItemOptional.get().getDescription());
	}

	@Test
	void deleteWalletItem() {
		Optional<Wallet> walletOptional = walletRepository.findById(savedWalletId);
		WalletItem walletItem = new WalletItem(null, walletOptional.get(), DATE, TYPE, DESCRIPTION, VALUE);

		repository.save(walletItem);

		repository.deleteById(walletItem.getId());

		Optional<WalletItem> response = repository.findById(walletItem.getId());

		Assertions.assertFalse(response.isPresent());
	}

	@Test
	void testFindBetweenDates() {
		Optional<Wallet> wallet = walletRepository.findById(savedWalletId);

		LocalDateTime localDateTime = DATE.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		Date currentDatePlusFiveDays = Date.from(localDateTime.plusDays(5).atZone(ZoneId.systemDefault()).toInstant());
		Date currentDatePlusSevenDays = Date.from(localDateTime.plusDays(7).atZone(ZoneId.systemDefault()).toInstant());

		repository.save(new WalletItem(null, wallet.get(), currentDatePlusFiveDays, TYPE, DESCRIPTION, VALUE));
		repository.save(new WalletItem(null, wallet.get(), currentDatePlusSevenDays, TYPE, DESCRIPTION, VALUE));

		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<WalletItem> response = repository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(savedWalletId, DATE, currentDatePlusFiveDays, pageRequest);

		Assertions.assertEquals(2, response.getContent().size());
		Assertions.assertEquals(2, response.getTotalElements());
		Assertions.assertEquals(savedWalletId, response.getContent().get(0).getWallet().getId());
	}

	@Test
	void testFindByType() {
		List<WalletItem> response = repository.findByWalletIdAndType(savedWalletId, TYPE);

		Assertions.assertEquals(1, response.size());
		Assertions.assertEquals(TYPE, response.get(0).getType());
	}

	@Test
	void testFindByTypeSd() {
		Optional<Wallet> wallet = walletRepository.findById(savedWalletId);

		repository.save(new WalletItem(null, wallet.get(), DATE, TypeEnum.SD, DESCRIPTION, VALUE));

		List<WalletItem> response = repository.findByWalletIdAndType(savedWalletId, TypeEnum.SD);

		Assertions.assertEquals(1, response.size());
		Assertions.assertEquals(TypeEnum.SD, response.get(0).getType());
	}

	@Test
	void testSumByWallet() {
		Optional<Wallet> wallet = walletRepository.findById(savedWalletId);

		repository.save(new WalletItem(null, wallet.get(), DATE, TYPE, DESCRIPTION, BigDecimal.valueOf(150.80)));

		BigDecimal response = repository.sumByWalletId(savedWalletId);

		Assertions.assertEquals(0, response.compareTo(BigDecimal.valueOf(215.8)));
	}
}