package com.ceos.spring_cgv_23rd.global.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

	@Override
	public void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		long startNanos = System.nanoTime();
		String traceId = generateOrExtractTraceId(request);

		MDC.put(MdcKeys.TRACE_ID, traceId);
		try {
			log.info("Request received. method={}, uri={}, query={}, ip={}",
				request.getMethod(),
				request.getRequestURI(),
				request.getQueryString(),
				extractClientIp(request));

			filterChain.doFilter(request, response);
		} finally {
			long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
			log.info("Request completed. method={}, uri={}, status={}, durationMs={}",
				request.getMethod(),
				request.getRequestURI(),
				response.getStatus(),
				durationMs);

			MDC.remove(MdcKeys.TRACE_ID);
		}
	}

	private String generateOrExtractTraceId(HttpServletRequest request) {
		String traceId = request.getHeader("X-Trace-Id");
		return traceId != null ? traceId : UUID.randomUUID().toString().substring(0, 8);
	}

	private String extractClientIp(HttpServletRequest request) {
		String xff = request.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isBlank()) {
			return xff.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	// health check 및 swagger 제외
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.startsWith("/actuator")
			|| uri.startsWith("/swagger-ui")
			|| uri.startsWith("/v3/api-docs");
	}
}
