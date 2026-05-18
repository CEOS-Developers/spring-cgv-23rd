package cgv_23rd.ceos.global.apiPayload.exception.handler;

import cgv_23rd.ceos.global.apiPayload.ApiResponse;
import cgv_23rd.ceos.global.apiPayload.code.GeneralErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j(topic = "AuthenticationEntryPoint")
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.warn("authentication required",
                kv("event", "authentication_required"),
                kv("uri", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("message", authException.getMessage()));

        GeneralErrorCode errorCode = GeneralErrorCode.MISSING_AUTH_INFO;

        ApiResponse<Void> apiResponse = ApiResponse.onFailure(errorCode, errorCode.getMessage());

        // 응답 설정
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // JSON으로 변환하여 응답 body에 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
