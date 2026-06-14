package com.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableCaching
@PropertySource("classpath:application.yml")
public class WalletApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}
}