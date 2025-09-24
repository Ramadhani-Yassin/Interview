package com.miniCore.BankingSystem.controller;

import com.miniCore.BankingSystem.model.User;
import com.miniCore.BankingSystem.model.UserRole;
import com.miniCore.BankingSystem.repository.UserRepository;
import com.miniCore.BankingSystem.security.JwtService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req) {
		try {
			Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.username, req.password));
			String token = jwtService.generateToken(auth.getName());
			return ResponseEntity.ok(new TokenResponse(token));
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(401).body("Invalid credentials");
		}
	}

	@PostMapping("/bootstrap-admin")
	public ResponseEntity<?> bootstrapAdmin(@RequestBody LoginRequest req) {
		User u = userRepository.findByUsername(req.username).orElseGet(User::new);
		u.setUsername(req.username);
		u.setPassword(passwordEncoder.encode(req.password));
		u.setRole(UserRole.ADMIN);
		u.setActive(true);
		userRepository.save(u);
		return ResponseEntity.ok("Admin ready");
	}

	public static class LoginRequest {
		@NotBlank public String username;
		@NotBlank public String password;
	}
	public static class TokenResponse {
		public String token;
		public TokenResponse(String token) { this.token = token; }
	}
} 