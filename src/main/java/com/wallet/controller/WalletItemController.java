package com.wallet.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.dto.WalletItemDTO;
import com.wallet.entity.UserWallet;
import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.response.Response;
import com.wallet.service.UserWalletService;
import com.wallet.service.WalletItemService;
import com.wallet.util.Util;
import com.wallet.util.enums.TypeEnum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("wallet-item")
@RequiredArgsConstructor
public class WalletItemController {

	private Logger logger = LoggerFactory.getLogger(WalletItemController.class);

	private final WalletItemService service;

	private final UserWalletService userWalletService;


	@PostMapping
	public ResponseEntity<Response<WalletItemDTO>> create(@Valid @RequestBody WalletItemDTO dto, BindingResult result) {
		Response<WalletItemDTO> response = new Response<>();
		if(result.hasErrors()) {
			result.getAllErrors().forEach(e -> response.getErrors().add(e.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		WalletItem walletItem = service.save(convertDtoToEntity(dto));
		response.setData(convertEntityToDto(walletItem));

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping(value = "/{wallet}")
	public ResponseEntity<Response<Page<WalletItemDTO>>> findBetweenDates(@PathVariable("wallet") Long wallet,
		@RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
		@RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
		@RequestParam(name = "page", defaultValue = "0") int page) {
			Response<Page<WalletItemDTO>> response = new Response<>();

			Optional<UserWallet> userWallet = userWalletService.findByUsersIdAndWalletId(Util.getAuthenticatedUserId(), wallet);
			if(!userWallet.isPresent()) {
				response.getErrors().add("Você não tem acesso a esta carteira");
				return ResponseEntity.badRequest().body(response);
			}

			Page<WalletItem> items = service.findBetweenDates(wallet, startDate, endDate, page);
			Page<WalletItemDTO> dto = items.map(this::convertEntityToDto);
			response.setData(dto);

			return ResponseEntity.ok(response);
	}

	@GetMapping(value = "/type/{wallet}")
	public ResponseEntity<Response<List<WalletItemDTO>>> findByWalletIdAndType(@PathVariable("wallet") Long wallet, @RequestParam("type") String type) {
		logger.info("Buscando por carteira {} e tipo {}", wallet, type);

		Response<List<WalletItemDTO>> response = new Response<>();
		List<WalletItem> list = service.findByWalletIdAndType(wallet, TypeEnum.getEnum(type));

		List<WalletItemDTO> dto = new ArrayList<>();
		list.forEach(walletItem -> dto.add(convertEntityToDto(walletItem)));
		response.setData(dto);
		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "/total/{wallet}")
	public ResponseEntity<Response<BigDecimal>> sumByWalletId(@PathVariable("wallet") Long wallet) {
		Response<BigDecimal> response = new Response<>();
		BigDecimal value = service.sumByWalletId(wallet);
		Optional<BigDecimal> data = Optional.ofNullable(value);
		response.setData(data.orElse(BigDecimal.ZERO));
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public ResponseEntity<Response<WalletItemDTO>> update(@Valid @RequestBody WalletItemDTO dto, BindingResult result) {
		Response<WalletItemDTO> response = new Response<>();
		
		
		Optional<WalletItem> walletItem = service.findById(dto.getId());
		
		if(!walletItem.isPresent()) {
			result.addError(new ObjectError("WalletItem", "WalletItem não encontrado"));
		} else {
			if(dto.getWallet() != null && walletItem.get().getWallet().getId().compareTo(dto.getWallet()) != 0) {
				result.addError(new ObjectError("WalletItemChanged", "Você não pode alterar a carteira"));
			}
		}

		if(result.hasErrors()) {
			result.getAllErrors().forEach(e -> response.getErrors().add(e.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		WalletItem saved = service.save(convertDtoToEntity(dto));
		response.setData(convertEntityToDto(saved));
		return ResponseEntity.ok(response);
	}

	@DeleteMapping(value = "/{walletItemId}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> delete(@PathVariable("walletItemId") Long walletItemId) {
		Response<String> response = new Response<>();
		Optional<WalletItem> walletItem = service.findById(walletItemId);
		if(!walletItem.isPresent()) {
			response.getErrors().add(String.format("Item da Carteira ID %d não encontrada", walletItemId));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		service.deleteById(walletItemId);
		response.setData(String.format("Item da Carteira ID %d apagada com sucesso", walletItemId));
		return ResponseEntity.ok(response);
	}

	private WalletItem convertDtoToEntity(WalletItemDTO dto) {
		WalletItem walletItem = new WalletItem();
		walletItem.setId(dto.getId());
		walletItem.setDate(dto.getDate());
		walletItem.setDescription(dto.getDescription());
		walletItem.setType(TypeEnum.getEnum(dto.getType()));
		walletItem.setValue(dto.getValue());
		Wallet wallet = new Wallet();
		wallet.setId(dto.getWallet());
		walletItem.setWallet(wallet);
		return walletItem;
	}

	private WalletItemDTO convertEntityToDto(WalletItem walletItem) {
		WalletItemDTO dto = new WalletItemDTO();
		dto.setId(walletItem.getId());
		dto.setDate(walletItem.getDate());
		dto.setDescription(walletItem.getDescription());
		dto.setType(walletItem.getType().getValue());
		dto.setValue(walletItem.getValue());
		dto.setWallet(walletItem.getWallet().getId());
		return dto;
	}
}