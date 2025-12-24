package com.example.itsme.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FirebaseAuthService {

	private final FirebaseAuth firebaseAuth;

	public FirebaseUser verify(String idToken) {
		try {
			FirebaseToken token = firebaseAuth.verifyIdToken(idToken, true);
			return FirebaseUser.from(token);
		}
		catch (FirebaseAuthException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Firebase ID token", ex);
		}
	}
}
