package com.wallet.util;

import java.util.Optional;

import com.wallet.entity.User;
import com.wallet.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Util {

	private static UserService staticService;

	public Util(@Autowired UserService service) {
		Util.staticService = service;
	}

	public static Long getAuthenticatedUserId() {
		try {
			Optional<User> user = staticService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
			if(user.isPresent()) {
				return user.get().getId();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}