package com.example.itsme.security;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.itsme.domain.Role;
import com.example.itsme.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret:change-me-please-change-this-key-to-a-random-256-bit-value}")
	private String jwtSecret;

	@Value("${jwt.expiration-ms:3600000}") // default 1 hour
	private long jwtExpirationMs;

	@Value("${jwt.refresh-expiration-ms:1209600000}") // default 14 days
	private long refreshExpirationMs;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public String generateAccessToken(User user) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtExpirationMs);

		Role role = user.getRole() != null ? user.getRole() : Role.USER;

		return Jwts.builder()
				.setSubject(String.valueOf(user.getUserId()))
				.claim("username", user.getUsername())
				.claim("email", user.getEmail())
				.claim("role", role.name())
				.setIssuedAt(now)
				.setExpiration(expiry)
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public String generateRefreshToken(User user) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + refreshExpirationMs);
		return Jwts.builder()
				.setSubject(String.valueOf(user.getUserId()))
				.claim("token_type", "refresh")
				.setIssuedAt(now)
				.setExpiration(expiry)
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);
		String subject = claims.getSubject();
		String role = claims.get("role", String.class);
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
		UserDetails principal = org.springframework.security.core.userdetails.User.withUsername(subject)
				.password("") // password not needed for JWT
				.authorities(Collections.singleton(authority))
				.build();
		return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
	}

	public boolean validateToken(String token) {
		if (!StringUtils.hasText(token)) {
			return false;
		}
		try {
			parseClaims(token);
			return true;
		}
		catch (Exception ex) {
			return false;
		}
	}

	public Long parseUserId(String token) {
		Claims claims = parseClaims(token);
		String subject = claims.getSubject();
		return Long.parseLong(subject);
	}

	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
}
