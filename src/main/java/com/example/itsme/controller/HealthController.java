package com.example.itsme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.health.CompositeHealth;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

	private final HealthEndpoint healthEndpoint;

	@GetMapping("/health")
	public ResponseEntity<Map<String, Object>> health() {
		HealthComponent component = healthEndpoint.health();
		Status status = component.getStatus();
		int httpStatus = Status.UP.equals(status) ? 200 : 503;

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", httpStatus); // 숫자 상태 코드로 반환 요청
		body.put("health", status.getCode());
		if (component instanceof Health health && !health.getDetails().isEmpty()) {
			body.put("details", health.getDetails());
		}
		if (component instanceof CompositeHealth composite && !composite.getComponents().isEmpty()) {
			Map<String, Object> components = new LinkedHashMap<>();
			composite.getComponents().forEach((name, hc) -> {
				Map<String, Object> item = new LinkedHashMap<>();
				item.put("health", hc.getStatus().getCode());
				if (hc instanceof Health h && !h.getDetails().isEmpty()) {
					item.put("details", h.getDetails());
				}
				components.put(name, item);
			});
			body.put("components", components);
		}
		return ResponseEntity.status(httpStatus).body(body);
	}
}
