package com.miniCore.BankingSystem.services;

import com.miniCore.BankingSystem.model.Customer;
import com.miniCore.BankingSystem.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CustomerService {
	private final CustomerRepository customerRepository;
	public CustomerService(CustomerRepository customerRepository) { this.customerRepository = customerRepository; }

	public Customer create(Customer c) { return customerRepository.save(c); }
	public List<Customer> list() { return customerRepository.findAll(); }
	public Customer get(Long id) { return customerRepository.findById(id).orElseThrow(); }

	public int bulkUploadCsv(MultipartFile file) {
		int count = 0;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
			String line; boolean headerSkipped = false;
			while ((line = br.readLine()) != null) {
				if (!headerSkipped) { headerSkipped = true; continue; }
				String[] parts = line.split(",");
				if (parts.length < 1) continue;
				Customer c = new Customer();
				c.setFullName(parts.length>0?parts[0].trim():null);
				c.setEmail(parts.length>1?parts[1].trim():null);
				c.setPhone(parts.length>2?parts[2].trim():null);
				c.setAddress(parts.length>3?parts[3].trim():null);
				customerRepository.save(c);
				count++;
			}
		} catch (Exception e) { throw new RuntimeException("CSV processing failed", e); }
		return count;
	}
} 