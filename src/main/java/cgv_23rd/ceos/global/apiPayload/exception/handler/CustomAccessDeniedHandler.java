package cgv_23rd.ceos.global.apiPayload.exception.handler;

import cgv_23rd.ceos.global.apiPayload.ApiResponse;
import cgv_23rd.ceos.global.apiPayload.code.GeneralErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(@Nonnull HttpServletRequest request,
                       @Nonnull HttpServletResponse response,
                       @Nonnull AccessDeniedException accessDeniedException) throws IOException {
        log.warn("access denied",
                kv("event", "access_denied"),
                kv("errorCode", GeneralErrorCode.FORBIDDEN.getCode()),
                kv("uri", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("message", accessDeniedException.getMessage()));

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        GeneralErrorCode errorCode = GeneralErrorCode.FORBIDDEN;
        ApiResponse<Object> apiResponse = ApiResponse.onFailure(errorCode, "해당 리소스에 대한 접근 권한이 없습니다.");

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
