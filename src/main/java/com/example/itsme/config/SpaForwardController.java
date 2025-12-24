package com.example.itsme.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

	// Match a path segment that is not an API/static prefix and contains no dots
	private static final String SPA_PATH_SEGMENT_REGEX =
			"^(?!api$)(?!actuator$)(?!v3$)(?!swagger-ui$)(?!webjars$)(?!error$)(?!assets$)[^\\.]*";

	@GetMapping({
			"/",
			"/{path:" + SPA_PATH_SEGMENT_REGEX + "}",
			"/{path:" + SPA_PATH_SEGMENT_REGEX + "}/**"
	})
	public String forward() {
		// Forward directly to the static index.html to avoid recursive dispatch.
		return "forward:/index.html";
	}
}