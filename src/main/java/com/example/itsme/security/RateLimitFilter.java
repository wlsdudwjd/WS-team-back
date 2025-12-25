package com.example.itsme.security;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

	private final StringRedisTemplate redisTemplate;

	@Value("${rate-limit.enabled:true}")
	private boolean enabled;

	@Value("${rate-limit.requests:100}")
	private int maxRequests;

	@Value("${rate-limit.window-seconds:60}")
	private int windowSeconds;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (!enabled || shouldBypass(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			if (isLimited(request)) {
				response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.getWriter().write("""
						{"status":429,"code":"TOO_MANY_REQUESTS","message":"Rate limit exceeded"}
						""");
				return;
			}
		}
		catch (DataAccessException ex) {
			// Redis unavailable → fail open
		}

		filterChain.doFilter(request, response);
	}

	private boolean isLimited(HttpServletRequest request) {
		String key = buildKey(request);
		ValueOperations<String, String> ops = redisTemplate.opsForValue();
		Long count = ops.increment(key);
		if (count != null && count == 1) {
			redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
		}
		return count != null && count > maxRequests;
	}

	private String buildKey(HttpServletRequest request) {
		String client = resolveClientId(request);
		long window = System.currentTimeMillis() / TimeUnit.SECONDS.toMillis(windowSeconds);
		return "rl:" + client + ":" + window;
	}

	private String resolveClientId(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");
		if (StringUtils.hasText(forwarded)) {
			int comma = forwarded.indexOf(',');
			return comma > 0 ? forwarded.substring(0, comma).trim() : forwarded.trim();
		}
		return request.getRemoteAddr();
	}

	private boolean shouldBypass(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.startsWith("/health") || path.startsWith("/swagger") || path.startsWith("/v3/api-docs");
	}
}
