package com.example.itsme.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {

	/**
	 * Base64-encoded service account JSON.
	 * Suggested env var: FIREBASE_CREDENTIALS_BASE64
	 */
	private String serviceAccountBase64;

	/**
	 * Absolute path to service account JSON file.
	 * Suggested env var: FIREBASE_CREDENTIALS_PATH
	 */
	private String serviceAccountPath;
}
