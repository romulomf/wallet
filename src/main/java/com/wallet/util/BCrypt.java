package com.wallet.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BCrypt {

	public static String getHash(String password) {
		if (password == null) {
			return null;
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(password);
	}
}