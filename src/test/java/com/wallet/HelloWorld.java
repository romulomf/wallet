package com.wallet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HelloWorld {

	public HelloWorld() {
		// Default constructor
	}

	@Test
	void testHelloWorld() {
		Assertions.assertEquals(1, 1);
	}
}