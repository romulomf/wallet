package com.wallet.security;

import com.wallet.entity.User;

public class JwtUserFactory {

	private JwtUserFactory() {}

	public static JwtUser create(User user) {
		return new JwtUser(user.getId(), user.getEmail(), user.getPassword());
	}
}