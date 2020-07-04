package com.wallet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.dto.UserDTO;
import com.wallet.entity.User;
import com.wallet.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class UserControllerTest {

	private static final Long ID = 1L;

	private static final String NAME = "User Test";

	private static final String PASSWORD = "123456";

	private static final String EMAIL = "email@teste.com";

	private static final String URL = "/user";

	@MockBean
	private UserService service;

	@Autowired
	private MockMvc mvc;

	@Test
	void testSave() throws Exception {
		BDDMockito.given(service.save(Mockito.any(User.class))).willReturn(getMockUser());
		mvc.perform(MockMvcRequestBuilders.post(URL).content(getJsonPayload(ID, NAME, PASSWORD, EMAIL))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(ID))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(NAME))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.password").value(PASSWORD))
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(EMAIL));
	}

	@Test
	public void testSaveInvalidUser() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post(URL).content(getJsonPayload(ID, NAME, PASSWORD, "email"))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("E-mail inválido"));
	}

	public User getMockUser() {
		User u = new User();
		u.setId(ID);
		u.setName(NAME);
		u.setPassword(PASSWORD);
		u.setEmail(EMAIL);
		return u;
	}

	public String getJsonPayload(Long id, String name, String password, String email) throws JsonProcessingException {
		UserDTO dto = new UserDTO();
		dto.setId(id);
		dto.setName(name);
		dto.setPassword(password);
		dto.setEmail(email);

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(dto);
	}
}