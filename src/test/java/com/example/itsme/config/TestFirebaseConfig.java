package com.example.itsme.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.google.firebase.auth.FirebaseAuth;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestFirebaseConfig {

	@Bean
	public FirebaseAuth firebaseAuth() {
		return mock(FirebaseAuth.class);
	}
}
