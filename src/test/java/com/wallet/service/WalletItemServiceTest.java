package com.wallet.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.repository.WalletItemRepository;
import com.wallet.util.enums.TypeEnum;

@SpringBootTest
@TestInstance(Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class WalletItemServiceTest {

	private static final Date DATE = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

	private static final TypeEnum TYPE = TypeEnum.EN;

	private static final String DESCRIPTION = "Conta de Luz";

	private static final BigDecimal VALUE = BigDecimal.valueOf(65);

	@MockBean
	private WalletItemRepository repository;

	@Autowired
	private WalletItemService service;

	@Test
	void testSave() {
		BDDMockito.given(repository.save(Mockito.any(WalletItem.class))).willReturn(getMockWalletItem());
		WalletItem response = service.save(new WalletItem());

		Assertions.assertNotNull(response);
		Assertions.assertEquals(DESCRIPTION, response.getDescription());
		Assertions.assertEquals(0, response.getValue().compareTo(VALUE));
	}

	@Test
	void testFindBetweenByDates() {
		List<WalletItem> list = new ArrayList<>();
		list.add(getMockWalletItem());
		Page<WalletItem> page = new PageImpl<>(list);
		BDDMockito.given(repository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(Mockito.anyLong(), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(PageRequest.class))).willReturn(page);
		Page<WalletItem> response = service.findBetweenDates(1L, DATE, DATE, 0);

		Assertions.assertNotNull(response);
		Assertions.assertEquals(1, response.getContent().size());
		Assertions.assertEquals(DESCRIPTION, response.getContent().get(0).getDescription());
	}

	@Test
	void testFindByType() {
		List<WalletItem> list = new ArrayList<>();
		list.add(getMockWalletItem());

		BDDMockito.given(repository.findByWalletIdAndType(Mockito.anyLong(), Mockito.any(TypeEnum.class))).willReturn(list);
	
		List<WalletItem> response = service.findByWalletIdAndType(1L, TypeEnum.EN);

		Assertions.assertNotNull(response);
		Assertions.assertEquals(TYPE, response.get(0).getType());
	}

	@Test
	void testSumByWallet() {
		BigDecimal value = BigDecimal.valueOf(45);

		BDDMockito.given(repository.sumByWalletId(Mockito.anyLong())).willReturn(value);

		BigDecimal response = service.sumByWalletId(1L);

		Assertions.assertEquals(0, response.compareTo(value));
	}

	private WalletItem getMockWalletItem() {
		Wallet wallet = new Wallet();
		wallet.setId(1L);
		WalletItem walletItem = new WalletItem(1L, wallet, DATE, TYPE, DESCRIPTION, VALUE);
		return walletItem;
	}
}