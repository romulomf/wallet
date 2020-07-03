package com.wallet.controller;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.dto.UserDTO;
import com.wallet.entity.User;
import com.wallet.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class UserControllerTest {

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
	mvc.perform(MockMvcRequestBuilders.post(URL).content(getJsonPayload()).contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    public User getMockUser() {
	User u = new User();
	u.setName(NAME);
	u.setPassword(PASSWORD);
	u.setEmail(EMAIL);
	return u;
    }

    public String getJsonPayload() throws JsonProcessingException {
	UserDTO dto = new UserDTO();
	dto.setName(NAME);
	dto.setPassword(PASSWORD);
	dto.setEmail(EMAIL);

	ObjectMapper mapper = new ObjectMapper();
	return mapper.writeValueAsString(dto);
    }
}