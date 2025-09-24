package com.miniCore.BankingSystem.repository;

import com.miniCore.BankingSystem.model.Account;
import com.miniCore.BankingSystem.model.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
	List<AccountTransaction> findByAccountOrderByCreatedAtDesc(Account account);
} 