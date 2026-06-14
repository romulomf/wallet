package com.wallet.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wallet.entity.User;
import com.wallet.repository.UserRepository;
import com.wallet.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository repository;

	@Override
	public User save(User u) {
		return repository.save(u);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return repository.findByEmailEquals(email);
	}
}