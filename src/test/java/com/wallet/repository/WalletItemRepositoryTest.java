package com.wallet.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

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

import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.util.enums.TypeEnum;

@SpringBootTest
@TestInstance(Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class WalletItemRepositoryTest {

	private static class WalletItemRepositoryTestErrorMessage {

		private static final String WALLET_ITEM_NOT_SAVED = "Não foi salvo o item da carteira, pois ele não tem um id definido.";
	}

	private static final Date DATE_TIME = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

	private static final TypeEnum TYPE = TypeEnum.EN;

	private static final String DESCRIPTION = "Conta de luz";

	private static final BigDecimal VALUE = BigDecimal.valueOf(65);

	private Wallet wallet;

	@Autowired
	private WalletItemRepository walletItemRepository;

	@Autowired
	private WalletRepository walletRepository;

	@BeforeEach
	void setUp() {
		this.wallet = walletRepository.save(new Wallet("Carteira Teste", BigDecimal.valueOf(250l)));
	}

	@AfterEach
	void tearDown() {
		walletItemRepository.deleteAll();
		walletRepository.deleteAll();
	}

	@Test
	void testSave() {
		WalletItem walletItem = new WalletItem(null, this.wallet, DATE_TIME, TYPE, DESCRIPTION, VALUE);
		walletItem = walletItemRepository.save(walletItem);

		Assertions.assertNotNull(walletItem);
		Assertions.assertEquals(DESCRIPTION, walletItem.getDescription());
		Assertions.assertEquals(TYPE, walletItem.getType());
		Assertions.assertEquals(VALUE, walletItem.getValue());
		Assertions.assertEquals(this.wallet.getId(), walletItem.getWallet().getId());
	}

	@Test
	void testSaveInvalidWalletItem() {
		WalletItem walletItem = new WalletItem(null, null, DATE_TIME, null, DESCRIPTION, null);
		Assertions.assertThrows(ConstraintViolationException.class, () -> walletItemRepository.save(walletItem));
	}

	@Test
	void testUpdate() {
		WalletItem walletItem = new WalletItem(null, this.wallet, DATE_TIME, TYPE, DESCRIPTION, VALUE);
		walletItem = walletItemRepository.save(walletItem);
		Assertions.assertNotNull(walletItem.getId(), WalletItemRepositoryTestErrorMessage.WALLET_ITEM_NOT_SAVED);
		
		Optional<WalletItem> walletItemSearchResult = walletItemRepository.findById(walletItem.getId());
		Assertions.assertTrue(walletItemSearchResult.isPresent(), String.format("Não foi encontrado o item da carteira %d", walletItem.getId()));

		final String updatedDescription = "Descrição Alterada";
		walletItemSearchResult.ifPresent(wi -> wi.setDescription(updatedDescription));
		walletItemRepository.save(walletItemSearchResult.get());

		walletItemSearchResult = walletItemRepository.findById(walletItem.getId());
		Assertions.assertTrue(walletItemSearchResult.isPresent(), String.format("Não foi encontrado o item da carteira %d", walletItem.getId()));
		
		Assertions.assertEquals(updatedDescription, walletItemSearchResult.get().getDescription());
	}

	@Test
	void deleteWalletItem() {
		WalletItem walletItem = new WalletItem(null, this.wallet, DATE_TIME, TYPE, DESCRIPTION, VALUE);
		walletItem = walletItemRepository.save(walletItem);
		Assertions.assertNotNull(walletItem.getId(), WalletItemRepositoryTestErrorMessage.WALLET_ITEM_NOT_SAVED);

		Optional<WalletItem> walletItemSearchResult = walletItemRepository.findById(walletItem.getId());
		Assertions.assertTrue(walletItemSearchResult.isPresent(), String.format("Não foi encontrado o item da carteira %d", walletItem.getId()));
		final WalletItem walletItemRetrieved = walletItemSearchResult.get();

		Assertions.assertDoesNotThrow(() -> walletItemRepository.deleteById(walletItemRetrieved.getId()));

		walletItemSearchResult = walletItemRepository.findById(walletItemRetrieved.getId());
		Assertions.assertTrue(walletItemSearchResult.isEmpty(), String.format("O item da carteira não foi excluído, pois ainda existe o registro com o id %d", walletItem.getId()));
	}

	@Test
	void testFindBetweenDates() {
		final Date currentDatePlusFiveDays = Date.from(LocalDateTime.now().plusDays(5).atZone(ZoneId.systemDefault()).toInstant());
		final Date currentDatePlusSevenDays = Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant());

		WalletItem item1 = WalletItem.builder()
			.wallet(wallet)
			.date(currentDatePlusFiveDays)
			.type(TYPE)
			.description(DESCRIPTION)
			.value(VALUE)
			.build();
		
		WalletItem item2 = WalletItem.builder()
			.wallet(wallet)
			.date(currentDatePlusSevenDays)
			.type(TYPE)
			.description(DESCRIPTION)
			.value(VALUE)
			.build();

		walletItemRepository.saveAndFlush(item1);
		walletItemRepository.saveAndFlush(item2);

		PageRequest pageRequest = PageRequest.of(0, 10);

		Page<WalletItem> page = walletItemRepository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(wallet.getId(), Date.from(Instant.now()), currentDatePlusFiveDays, pageRequest);

		Assertions.assertEquals(1, page.getContent().size());
		Assertions.assertEquals(1, page.getTotalElements());

		Assertions.assertEquals(this.wallet.getId(), page.getContent().get(0).getWallet().getId());
	}

	@Test
	void testFindByType() {
		WalletItem walletItem = WalletItem.builder()
				.wallet(wallet)
				.date(DATE_TIME)
				.type(TYPE)
				.description(DESCRIPTION)
				.value(VALUE)
				.build();
		walletItem = walletItemRepository.save(walletItem);
		Assertions.assertNotNull(walletItem.getId(), WalletItemRepositoryTestErrorMessage.WALLET_ITEM_NOT_SAVED);

		List<WalletItem> walletItems = walletItemRepository.findByWalletIdAndType(wallet.getId(), TYPE);

		Assertions.assertEquals(1, walletItems.size());
		Assertions.assertEquals(TYPE, walletItems.get(0).getType(), String.format("O tipo do item da carteira %d não é de %s", walletItems.get(0).getId(), TYPE));
	}

	@Test
	void testSumByWallet() {
		final BigDecimal value1 = BigDecimal.valueOf(135.10);
		final BigDecimal value2 = BigDecimal.valueOf(150.80);
		
		WalletItem item1 = WalletItem.builder()
				.wallet(wallet)
				.date(DATE_TIME)
				.type(TYPE)
				.description("Item 1")
				.value(value1)
				.build();

		WalletItem item2 = WalletItem.builder()
				.wallet(wallet)
				.date(DATE_TIME)
				.type(TYPE)
				.description("Item 2")
				.value(value2)
				.build();
		
		item1 = walletItemRepository.save(item1);
		item2 = walletItemRepository.save(item2);

		Assertions.assertNotNull(item1.getId(), WalletItemRepositoryTestErrorMessage.WALLET_ITEM_NOT_SAVED);
		Assertions.assertNotNull(item2.getId(), WalletItemRepositoryTestErrorMessage.WALLET_ITEM_NOT_SAVED);

		BigDecimal actualSum = walletItemRepository.sumByWalletId(this.wallet.getId());

		Assertions.assertEquals(0, value1.add(value2).compareTo(actualSum));
	}
}