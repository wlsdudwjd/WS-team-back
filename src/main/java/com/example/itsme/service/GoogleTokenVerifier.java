package com.example.itsme.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleTokenVerifier {

	private final GoogleIdTokenVerifier verifier;

	public GoogleTokenVerifier(@Value("${google.client-id:}") String clientId) {
		this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
				.setAudience(Collections.singletonList(clientId))
				.build();
	}

	public GoogleUser verify(String idToken) {
		try {
			GoogleIdToken token = verifier.verify(idToken);
			if (token == null) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google ID token");
			}
			Payload payload = token.getPayload();
			return new GoogleUser(payload.getSubject(),
					payload.getEmail(),
					(String) payload.get("name"));
		}
		catch (GeneralSecurityException | IOException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google ID token", ex);
		}
	}

	public record GoogleUser(String sub, String email, String name) {
	}
}
