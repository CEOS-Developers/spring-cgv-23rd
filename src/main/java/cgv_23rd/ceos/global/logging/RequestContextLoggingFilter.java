package cgv_23rd.ceos.global.logging;

import cgv_23rd.ceos.global.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
public class RequestContextLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String METHOD = "method";
    private static final String URI = "uri";
    private static final String CLIENT_IP = "clientIp";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startedAt = System.currentTimeMillis();

        MDC.put(REQUEST_ID, resolveRequestId(request));
        MDC.put(METHOD, request.getMethod());
        MDC.put(URI, request.getRequestURI());
        MDC.put(CLIENT_IP, resolveClientIp(request));

        try {
            filterChain.doFilter(request, response);
        } finally {
            enrichAuthenticatedUser();

            if (!isActuatorRequest(request)) {
                log.info("request completed",
                        kv("event", "request_completed"),
                        kv("method", request.getMethod()),
                        kv("uri", request.getRequestURI()),
                        kv("status", response.getStatus()),
                        kv("durationMs", System.currentTimeMillis() - startedAt));
            }
            MDC.clear();
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String headerRequestId = request.getHeader("X-Request-Id");
        if (headerRequestId != null && !headerRequestId.isBlank()) {
            return headerRequestId;
        }
        return UUID.randomUUID().toString();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void enrichAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            MDC.put(USER_ID, String.valueOf(userDetails.getUser().getId()));
        }
    }

    private boolean isActuatorRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator");
    }
}
