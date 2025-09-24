package com.miniCore.BankingSystem.services;

import com.miniCore.BankingSystem.model.*;
import com.miniCore.BankingSystem.repository.AccountRepository;
import com.miniCore.BankingSystem.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
	private final AccountRepository accountRepository;
	private final CustomerRepository customerRepository;
	public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
		this.accountRepository = accountRepository;
		this.customerRepository = customerRepository;
	}

	@Transactional
	public Account createAccount(Long customerId, AccountType type) {
		Customer customer = customerRepository.findById(customerId).orElseThrow();
		Account acc = new Account();
		acc.setCustomer(customer);
		acc.setType(type);
		acc.setBalance(new BigDecimal("0.00"));
		acc.setAccountNumber(generateAccountNumber());
		return accountRepository.save(acc);
	}

	public List<Account> getCustomerAccounts(Long customerId) {
		Customer customer = customerRepository.findById(customerId).orElseThrow();
		return accountRepository.findByCustomer(customer);
	}

	public Account getByAccountNumber(String accountNumber) { return accountRepository.findByAccountNumber(accountNumber).orElseThrow(); }

	private String generateAccountNumber() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
	}
} 