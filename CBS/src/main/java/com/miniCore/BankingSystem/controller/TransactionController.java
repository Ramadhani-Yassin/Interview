package com.miniCore.BankingSystem.controller;

import com.miniCore.BankingSystem.services.TransactionService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
	private final TransactionService transactionService;
	public TransactionController(TransactionService transactionService) { this.transactionService = transactionService; }

	@PostMapping("/credit")
	@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
	public ResponseEntity<?> credit(@RequestBody CreditDebitRequest req) {
		transactionService.credit(req.accountNumber, req.amount, req.description);
		return ResponseEntity.ok("credited");
	}

	@PostMapping("/debit")
	@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
	public ResponseEntity<?> debit(@RequestBody CreditDebitRequest req) {
		transactionService.debit(req.accountNumber, req.amount, req.description);
		return ResponseEntity.ok("debited");
	}

	@PostMapping("/transfer/request")
	@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
	public ResponseEntity<?> requestTransfer(@RequestBody TransferRequest req, @RequestHeader("X-Requested-By") String requestedBy) {
		transactionService.requestTransfer(req.fromAccount, req.toAccount, req.amount, requestedBy);
		return ResponseEntity.ok("transfer pending approval");
	}

	@PostMapping("/transfer/approve")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> approveTransfer(@RequestBody TransferRequest req, @RequestHeader("X-Approved-By") String approver) {
		transactionService.approveTransfer(req.fromAccount, req.toAccount, req.amount, approver);
		return ResponseEntity.ok("approved");
	}

	public static class CreditDebitRequest {
		@NotBlank public String accountNumber;
		@NotNull public BigDecimal amount;
		public String description;
	}
	public static class TransferRequest {
		@NotBlank public String fromAccount;
		@NotBlank public String toAccount;
		@NotNull public BigDecimal amount;
	}
} 