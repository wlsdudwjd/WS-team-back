package com.example.itsme.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards non-API routes to index.html so the SPA can handle routing.
 */
@Controller
public class SpaForwardController {

	@GetMapping("/{path:^(?!api|actuator|v3|swagger-ui|webjars|error).*$}")
	public String forward() {
		return "forward:/";
	}
}
