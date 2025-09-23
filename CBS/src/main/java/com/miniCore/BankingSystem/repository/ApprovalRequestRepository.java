package com.miniCore.BankingSystem.repository;

import com.miniCore.BankingSystem.model.ApprovalRequest;
import com.miniCore.BankingSystem.model.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {
	List<ApprovalRequest> findByStatusOrderByCreatedAtAsc(ApprovalStatus status);
} 