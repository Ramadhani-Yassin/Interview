package com.miniCore.BankingSystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
	@Value("${security.jwt.secret}")
	private String secret;
	@Value("${security.jwt.expiration-minutes}")
	private long expirationMinutes;
	@Value("${security.jwt.issuer}")
	private String issuer;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String username) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMinutes * 60_000);
		return Jwts.builder()
			.setSubject(username)
			.setIssuer(issuer)
			.setIssuedAt(now)
			.setExpiration(exp)
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	public String extractUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
			.parseClaimsJws(token).getBody().getSubject();
	}
} 