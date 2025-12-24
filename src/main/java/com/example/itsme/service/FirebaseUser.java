package com.example.itsme.service;

import com.google.firebase.auth.FirebaseToken;

public record FirebaseUser(
		String uid,
		String email,
		String displayName,
		String phoneNumber
) {
	static FirebaseUser from(FirebaseToken token) {
		String phone = null;
		Object claimPhone = token.getClaims().get("phone_number");
		if (claimPhone instanceof String s) {
			phone = s;
		}
		return new FirebaseUser(
				token.getUid(),
				token.getEmail(),
				token.getName(),
				phone);
	}
}
