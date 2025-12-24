package com.example.itsme.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
@RequiredArgsConstructor
public class FirebaseConfig {

	private final FirebaseProperties firebaseProperties;

	@Bean
	public FirebaseApp firebaseApp() throws IOException {
		if (FirebaseApp.getApps().isEmpty()) {
			GoogleCredentials credentials = loadCredentials();
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(credentials)
					.build();
			return FirebaseApp.initializeApp(options);
		}
		return FirebaseApp.getInstance();
	}

	private GoogleCredentials loadCredentials() throws IOException {
		// Prefer base64 if present
		if (StringUtils.hasText(firebaseProperties.getServiceAccountBase64())) {
			byte[] decoded = Base64.getDecoder().decode(firebaseProperties.getServiceAccountBase64());
			return GoogleCredentials.fromStream(new ByteArrayInputStream(decoded));
		}
		// Fallback: file path
		if (StringUtils.hasText(firebaseProperties.getServiceAccountPath())) {
			Path path = Path.of(firebaseProperties.getServiceAccountPath());
			if (!Files.exists(path)) {
				throw new IllegalStateException("Firebase credentials file not found: " + path);
			}
			return GoogleCredentials.fromStream(Files.newInputStream(path));
		}
		throw new IllegalStateException(
				"Missing Firebase credentials. Set firebase.service-account-base64/FIREBASE_CREDENTIALS_BASE64 or firebase.service-account-path/FIREBASE_CREDENTIALS_PATH.");
	}

	@Bean
	public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
		return FirebaseAuth.getInstance(firebaseApp);
	}
}
