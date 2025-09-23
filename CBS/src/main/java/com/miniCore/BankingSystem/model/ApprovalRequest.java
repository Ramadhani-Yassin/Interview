package com.miniCore.BankingSystem.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "approvals")
public class ApprovalRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "approval_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "action_type", nullable = false, length = 20)
	private ApprovalAction actionType;

	@Column(name = "reference_id", nullable = false)
	private Long referenceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requested_by")
	private User requestedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approved_by")
	private User approvedBy;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ApprovalStatus status = ApprovalStatus.PENDING;

	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public ApprovalAction getActionType() { return actionType; }
	public void setActionType(ApprovalAction actionType) { this.actionType = actionType; }
	public Long getReferenceId() { return referenceId; }
	public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
	public User getRequestedBy() { return requestedBy; }
	public void setRequestedBy(User requestedBy) { this.requestedBy = requestedBy; }
	public User getApprovedBy() { return approvedBy; }
	public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }
	public ApprovalStatus getStatus() { return status; }
	public void setStatus(ApprovalStatus status) { this.status = status; }
	public Instant getCreatedAt() { return createdAt; }
	public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
} 