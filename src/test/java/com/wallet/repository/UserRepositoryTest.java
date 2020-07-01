package com.wallet.repository;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wallet.entity.User;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class UserRepositoryTest {

	private static final String EMAIL = "email@test.com";

	public UserRepositoryTest() {
		// Default constructor
	}

	@Autowired
	UserRepository repository;

	@BeforeAll
	public void setUp() {
		User u = new User();
		u.setName("Set Up  User");
		u.setPassword("Senha123");
		u.setEmail(EMAIL);
		
		repository.save(u);
	}

	@AfterAll
	public void tearDown() {
		repository.deleteAll();
	}

	@Test
	void testSave() {
		User u = new User();
		u.setName("Teste");
		u.setPassword("123456");
		u.setEmail("teste@teste.com");

		User response = repository.save(u);

		Assertions.assertNotNull(response);
	}

	void testFindByEmail() {
		Optional<User> response = repository.findByEmailEquals(EMAIL);
		
		Assertions.assertTrue(response.isPresent());
		Assertions.assertEquals(response.get().getEmail(), EMAIL);
	}
}