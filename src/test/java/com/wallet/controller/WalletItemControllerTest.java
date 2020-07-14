package com.wallet.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.dto.WalletItemDTO;
import com.wallet.entity.User;
import com.wallet.entity.UserWallet;
import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.service.UserService;
import com.wallet.service.UserWalletService;
import com.wallet.service.WalletItemService;
import com.wallet.util.enums.TypeEnum;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_METHOD)
class WalletItemControllerTest {

	private static final Long ID = 1L;

	private static final Date DATE = new Date();

	private static final LocalDate TODAY = LocalDate.now();

	private static final TypeEnum TYPE = TypeEnum.EN;

	private static final String DESCRIPTION = "Conta de Luz";

	private static final BigDecimal VALUE = BigDecimal.valueOf(65);

	private static final String URL = "/wallet-item";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private WalletItemService service;

	@MockBean
	private UserService userService;

	@MockBean
	private UserWalletService userWalletService;

	@Test
	@Order(1)
	@WithMockUser
	void testSave() throws Exception {
		BDDMockito.given(service.save(Mockito.any(WalletItem.class))).willReturn(getMockedWalletItem());

		mvc.perform(MockMvcRequestBuilders.post(URL).content(getJsonPayload()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(ID))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.date").value(TODAY.format(getDateFormatter())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(DESCRIPTION))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.type").value(TYPE.getValue()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.value").value(VALUE))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.wallet").value(ID));
	}

	@Test
	@Order(2)
	@WithMockUser
	void testFindBetweenDates() throws Exception {
		List<WalletItem> list = new ArrayList<>();
		list.add(getMockedWalletItem());
		Page<WalletItem> page = new PageImpl<>(list);

		String startDate = TODAY.format(getDateFormatter());
		String endDate = TODAY.plusDays(5).format(getDateFormatter());

		User user = new User();
		user.setId(1L);

		BDDMockito.given(service.findBetweenDates(Mockito.anyLong(), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.anyInt())).willReturn(page);
		BDDMockito.given(userService.findByEmail(Mockito.anyString())).willReturn(Optional.of(user));
		BDDMockito.given(userWalletService.findByUsersIdAndWalletId(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.of(new UserWallet()));

		mvc.perform(MockMvcRequestBuilders.get(URL + "/1?startDate=" + startDate + "&endDate=" + endDate).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].id").value(ID))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].date").value(TODAY.format(getDateFormatter())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].description").value(DESCRIPTION))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].type").value(TYPE.getValue()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].value").value(VALUE))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].wallet").value(ID));
	}

	@Test
	@Order(3)
	@WithMockUser
	void testFindByType() throws Exception {
		List<WalletItem> list = new ArrayList<>();
		list.add(getMockedWalletItem());

		BDDMockito.given(service.findByWalletIdAndType(Mockito.anyLong(), Mockito.any(TypeEnum.class))).willReturn(list);

		mvc.perform(MockMvcRequestBuilders.get(URL + "/type/1?type=ENTRADA").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].id").value(ID))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].date").value(TODAY.format(getDateFormatter())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].description").value(DESCRIPTION))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].type").value(TYPE.getValue()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].value").value(VALUE))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].wallet").value(ID));
	}

	@Test
	@Order(4)
	@WithMockUser
	void testSumByWallet() throws Exception {
		BigDecimal value = BigDecimal.valueOf(536.90);

		BDDMockito.given(service.sumByWalletId(Mockito.anyLong())).willReturn(value);

		mvc.perform(MockMvcRequestBuilders.get(URL + "/total/1").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data").value("536.9"));
	}

	@Test
	@Order(5)
	@WithMockUser
	void testUpdate() throws Exception {
		String description = "Nova descrição";
		Wallet wallet = new Wallet();
		wallet.setId(ID);

		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.of(getMockedWalletItem()));
		BDDMockito.given(service.save(Mockito.any(WalletItem.class))).willReturn(new WalletItem(1L, wallet, DATE, TypeEnum.SD, description, VALUE));

		mvc.perform(MockMvcRequestBuilders.put(URL).content(getJsonPayload()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(ID))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.date").value(TODAY.format(getDateFormatter())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(description))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.type").value(TypeEnum.SD.getValue()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.value").value(VALUE))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.wallet").value(ID));
	}

	@Test
	@Order(6)
	@WithMockUser
	void testUpdateWalletChange() throws Exception {
		Wallet wallet = new Wallet();
		wallet.setId(99L);

		WalletItem walletItem = new WalletItem(1L, wallet, DATE, TypeEnum.SD, DESCRIPTION, VALUE);

		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.of(walletItem));

		mvc.perform(MockMvcRequestBuilders.put(URL).content(getJsonPayload()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("Você não pode alterar a carteira")) ;
	}

	@Test
	@Order(7)
	@WithMockUser
	void testUpdateInvalidId() throws Exception {
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

		mvc.perform(MockMvcRequestBuilders.put(URL).content(getJsonPayload()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("WalletItem não encontrado"));
	}

	@Test
	@Order(8)
	@WithMockUser
	void testDelete() throws Exception {
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.of(new WalletItem()));

		mvc.perform(MockMvcRequestBuilders.delete(URL + "/1").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data").value(String.format("Item da Carteira ID %d apagada com sucesso", ID)));
	}

	@Test
	@Order(9)
	@WithMockUser
	void testDeleteInvalidId() throws Exception {
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

		mvc.perform(MockMvcRequestBuilders.delete(URL + "/99").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isNotFound())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("Item da Carteira ID 99 não encontrada"));
	}

	private WalletItem getMockedWalletItem() {
		Wallet wallet = new Wallet();
		wallet.setId(1L);

		WalletItem walletItem = new WalletItem(1L, wallet, DATE, TYPE, DESCRIPTION, VALUE);
		return walletItem;
	}

	private DateTimeFormatter getDateFormatter() {
		return DateTimeFormatter.ofPattern("dd-MM-yyyy");
	}

	String getJsonPayload() throws JsonProcessingException {
		WalletItemDTO dto = new WalletItemDTO();
		dto.setId(ID);
		dto.setDate(DATE);
		dto.setDescription(DESCRIPTION);
		dto.setType(TYPE.getValue());
		dto.setValue(VALUE);
		dto.setWallet(ID);

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(dto);
	}
}