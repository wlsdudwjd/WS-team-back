package com.example.itsme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Firebase ID token login payload")
public record FirebaseLoginRequest(
		@Schema(description = "Firebase ID token", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank String idToken,

		@Schema(description = "Preferred username (아이디). 미입력 시 이메일 앞부분을 기본값으로 사용")
		@NotBlank String username,

		@Schema(description = "이름. 미입력 시 Firebase displayName 또는 이메일 앞부분을 사용")
		@NotBlank String name,

		@Schema(description = "전화번호. 미입력 시 Firebase phoneNumber를 사용")
		@NotBlank String phone
) {
}
