package cgv_23rd.ceos.global.apiPayload.exception.handler;

import cgv_23rd.ceos.global.apiPayload.ApiResponse;
import cgv_23rd.ceos.global.apiPayload.code.BaseErrorCode;
import cgv_23rd.ceos.global.apiPayload.code.GeneralErrorCode;
import cgv_23rd.ceos.global.apiPayload.exception.GeneralException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ExceptionAdvice {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(GeneralException e, HttpServletRequest request) {
        log.warn("business exception occurred",
                kv("event", "business_exception"),
                kv("errorCode", e.getCode().getCode()),
                kv("message", e.getMessage() != null ? e.getMessage() : e.getCode().getMessage()),
                kv("uri", request.getRequestURI()),
                kv("method", request.getMethod()));
        BaseErrorCode code = e.getCode();
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, e.getMessage()));
    }

    // @Valid 바디 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        var errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> String.format("[%s] %s (입력값: %s)",
                        fe.getField(),
                        fe.getDefaultMessage(),
                        fe.getRejectedValue()))
                .toList();

        log.warn("validation failed",
                kv("event", "validation_failed"),
                kv("errorCode", GeneralErrorCode.INVALID_PARAMETER.getCode()),
                kv("uri", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("errors", errors));

        BaseErrorCode code = GeneralErrorCode.INVALID_PARAMETER;
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(code, errors));
    }

    // @PathVariable, @RequestParam 등 파라미터 제약 조건 위반 처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        List<String> errors = e.getConstraintViolations().stream()
                .map(violation -> String.format("[%s] %s (입력값: %s)",
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .collect(Collectors.toList());

        log.warn("constraint violation",
                kv("event", "constraint_violation"),
                kv("errorCode", GeneralErrorCode.INVALID_PARAMETER.getCode()),
                kv("uri", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("errors", errors));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_PARAMETER, errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleJsonErrors(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("json parse error",
                kv("event", "json_parse_error"),
                kv("errorCode", GeneralErrorCode.INVALID_PARAMETER.getCode()),
                kv("uri", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("message", e.getMessage()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_PARAMETER, "입력값이 잘못되었습니다. (JSON 형식을 확인해주세요)"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("data integrity violation",
                kv("event", "data_integrity_violation"),
                kv("errorCode", GeneralErrorCode.INVALID_PARAMETER.getCode()),
                kv("uri", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("message", e.getMessage()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_PARAMETER, "이미 처리되었거나 중복된 요청입니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e, HttpServletRequest request) {
        log.error("unhandled exception occurred",
                kv("event", "internal_server_error"),
                kv("errorCode", GeneralErrorCode.INTERNAL_SERVER_ERROR.getCode()),
                kv("uri", request.getRequestURI()),
                kv("method", request.getMethod()),
                kv("exceptionType", e.getClass().getName()),
                kv("message", e.getMessage()), e);
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code, code.getMessage()));
    }

}
