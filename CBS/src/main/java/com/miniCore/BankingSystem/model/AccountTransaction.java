package com.miniCore.BankingSystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions", indexes = {
	@Index(name = "idx_txn_account", columnList = "account_id"),
	@Index(name = "idx_txn_createdAt", columnList = "createdAt")
})
public class AccountTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	private Account account;

	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", nullable = false, length = 20)
	private TransactionType type;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Column(name = "balance_after", nullable = false, precision = 15, scale = 2)
	private BigDecimal balanceAfter;

	@Column(length = 255)
	private String description;

	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Account getAccount() { return account; }
	public void setAccount(Account account) { this.account = account; }
	public TransactionType getType() { return type; }
	public void setType(TransactionType type) { this.type = type; }
	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }
	public BigDecimal getBalanceAfter() { return balanceAfter; }
	public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public Instant getCreatedAt() { return createdAt; }
	public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
} 