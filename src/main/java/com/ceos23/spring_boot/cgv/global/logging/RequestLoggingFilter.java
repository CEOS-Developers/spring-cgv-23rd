package com.ceos23.spring_boot.cgv.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.MDC;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String METHOD_KEY = "method";
    private static final String PATH_KEY = "path";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String requestId = resolveRequestId(request);

        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put(METHOD_KEY, request.getMethod());
        MDC.put(PATH_KEY, request.getRequestURI());
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long latencyMs = System.currentTimeMillis() - startTime;
            logRequestCompletion(request, response, latencyMs);
            MDC.remove(REQUEST_ID_KEY);
            MDC.remove(METHOD_KEY);
            MDC.remove(PATH_KEY);
        }
    }

    private void logRequestCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            long latencyMs
    ) {
        String userId = resolveAuthenticatedUserId();
        int status = response.getStatus();

        if (status >= 500) {
            log.error(
                    "event=request_complete method={} path={} status={} latencyMs={} userId={} clientIp={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    latencyMs,
                    userId,
                    request.getRemoteAddr()
            );
            return;
        }

        if (status >= 400) {
            log.warn(
                    "event=request_complete method={} path={} status={} latencyMs={} userId={} clientIp={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    latencyMs,
                    userId,
                    request.getRemoteAddr()
            );
            return;
        }

        log.info(
                "event=request_complete method={} path={} status={} latencyMs={} userId={} clientIp={}",
                request.getMethod(),
                request.getRequestURI(),
                status,
                latencyMs,
                userId,
                request.getRemoteAddr()
        );
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);

        if (StringUtils.hasText(requestId)) {
            return requestId;
        }

        return UUID.randomUUID().toString();
    }

    private String resolveAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof com.ceos23.spring_boot.cgv.global.security.CustomUserDetails userDetails) {
            return String.valueOf(userDetails.getUserId());
        }

        return Optional.ofNullable(principal)
                .map(Object::toString)
                .orElse("anonymous");
    }
}
