package com.miniCore.BankingSystem.controller;

import com.miniCore.BankingSystem.model.Account;
import com.miniCore.BankingSystem.model.AccountType;
import com.miniCore.BankingSystem.services.AccountService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
	private final AccountService accountService;
	public AccountController(AccountService accountService) { this.accountService = accountService; }

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Account> create(@RequestBody CreateAccountRequest req) {
		return ResponseEntity.ok(accountService.createAccount(req.customerId, req.type));
	}

	@GetMapping("/by-customer/{customerId}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<List<Account>> byCustomer(@PathVariable Long customerId) {
		return ResponseEntity.ok(accountService.getCustomerAccounts(customerId));
	}

	public static class CreateAccountRequest {
		@NotNull public Long customerId;
		@NotNull public AccountType type;
	}
} 