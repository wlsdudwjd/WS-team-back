package com.example.itsme.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

	private final PasswordEncoder encoder = new BCryptPasswordEncoder();

	public String hash(String raw) {
		return encoder.encode(raw);
	}

	public boolean matches(String raw, String hashed) {
		return encoder.matches(raw, hashed);
	}

	public boolean isHashed(String value) {
		return value != null && value.startsWith("$2");
	}
}
