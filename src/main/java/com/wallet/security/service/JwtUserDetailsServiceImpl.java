package com.wallet.security.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.wallet.entity.User;
import com.wallet.security.JwtUserFactory;
import com.wallet.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUserDetailsServiceImpl implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String email) {
		Optional<User> user = userService.findByEmail(email);
		if (user.isPresent()) {
			return JwtUserFactory.create(user.get());
		}
		throw new UsernameNotFoundException("E-mail não encontrado");
	}
}