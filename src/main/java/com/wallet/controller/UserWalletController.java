package com.wallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.dto.UserWalletDTO;
import com.wallet.entity.User;
import com.wallet.entity.UserWallet;
import com.wallet.entity.Wallet;
import com.wallet.response.Response;
import com.wallet.service.UserWalletService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("user-wallet")
public class UserWalletController {

	@Autowired
	private UserWalletService service;

	@PostMapping
	public ResponseEntity<Response<UserWalletDTO>> create(@Valid @RequestBody UserWalletDTO dto, BindingResult result) {
		Response<UserWalletDTO> response = new Response<>();
		if(result.hasErrors()) {
			result.getAllErrors().forEach(e -> response.getErrors().add(e.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		UserWallet userWallet = service.save(convertDtoToEntity(dto));
		response.setData(convertEntityToDto(userWallet));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	public UserWallet convertDtoToEntity(UserWalletDTO dto) {
		UserWallet userWallet = new UserWallet();
		userWallet.setId(dto.getId());
		User user = new User();
		user.setId(dto.getUsers());
		userWallet.setUsers(user);
		Wallet wallet = new Wallet();
		wallet.setId(dto.getWallet());
		userWallet.setWallet(wallet);
		return userWallet;
	}

	public UserWalletDTO convertEntityToDto(UserWallet userWallet) {
		UserWalletDTO dto = new UserWalletDTO();
		dto.setId(userWallet.getId());
		dto.setUsers(userWallet.getUsers().getId());
		dto.setWallet(userWallet.getWallet().getId());
		return dto;
	}
}