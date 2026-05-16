package com.ceos23.spring_boot.cgv.global.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class AuditLogService {

    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("AUDIT_LOGGER");
    private static final Logger INTERNAL_LOGGER = LoggerFactory.getLogger(AuditLogService.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public void info(String event, Map<String, Object> fields) {
        write("INFO", event, fields, null);
    }

    public void warn(String event, Map<String, Object> fields) {
        write("WARN", event, fields, null);
    }

    public void error(String event, Map<String, Object> fields, Throwable throwable) {
        write("ERROR", event, fields, throwable);
    }

    private void write(String level, String event, Map<String, Object> fields, Throwable throwable) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now().toString());
        payload.put("level", level);
        payload.put("event", event);
        payload.put("requestId", MDC.get("requestId"));
        payload.put("method", MDC.get("method"));
        payload.put("path", MDC.get("path"));
        payload.putAll(fields);

        if (throwable != null) {
            payload.put("exception", throwable.getClass().getSimpleName());
            payload.put("message", throwable.getMessage());
        }

        try {
            AUDIT_LOGGER.info(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException exception) {
            INTERNAL_LOGGER.warn("Failed to serialize audit log. event={}", event, exception);
        }
    }
}
