package com.miniCore.BankingSystem.controller;

import com.miniCore.BankingSystem.model.Customer;
import com.miniCore.BankingSystem.services.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
	private final CustomerService customerService;
	public CustomerController(CustomerService customerService) { this.customerService = customerService; }

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Customer> create(@RequestBody Customer c) {
		return ResponseEntity.ok(customerService.create(c));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<List<Customer>> list() { return ResponseEntity.ok(customerService.list()); }

	@PostMapping("/upload")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
		int count = customerService.bulkUploadCsv(file);
		return ResponseEntity.ok("Imported " + count + " customers");
	}
} 