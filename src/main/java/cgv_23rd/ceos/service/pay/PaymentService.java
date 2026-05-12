package cgv_23rd.ceos.service.pay;

import cgv_23rd.ceos.dto.payment.request.InstantPaymentRequest;
import cgv_23rd.ceos.dto.payment.response.PaymentResponse;
import cgv_23rd.ceos.global.apiPayload.code.GeneralErrorCode;
import cgv_23rd.ceos.global.apiPayload.exception.GeneralException;
import cgv_23rd.ceos.global.config.PaymentProperties;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProperties paymentProperties;
    private final PaymentFeignClient paymentFeignClient; // RestTemplate 대신 FeignClient 주입

    public PaymentResponse requestInstantPayment(String paymentId, String orderName, int amount, String customData) {
        long startedAt = System.currentTimeMillis();
        InstantPaymentRequest request = new InstantPaymentRequest(
                paymentProperties.storeId(),
                orderName,
                amount,
                "KRW",
                customData
        );

        log.info("Payment instant request started. paymentId={}, amount={}, orderName={}",
                paymentId, amount, orderName);

        try {
            // Feign 인터페이스 호출
            PaymentResponse response = paymentFeignClient.requestInstantPayment(paymentId, request);
            if (response == null || response.data() == null) {
                log.warn("Payment instant request returned empty success body. paymentId={}, durationMs={}",
                        paymentId, System.currentTimeMillis() - startedAt);
                return getPayment(paymentId);
            }
            log.info("Payment instant request completed. paymentId={}, paymentStatus={}, durationMs={}",
                    paymentId, response.data().paymentStatus(), System.currentTimeMillis() - startedAt);
            return response;
        } catch (FeignException e) {
            log.warn("Payment instant request failed. paymentId={}, status={}, durationMs={}, body={}",
                    paymentId, e.status(), System.currentTimeMillis() - startedAt, safeBody(e));
            if (isEmptySuccessResponse(e)) {
                return getPayment(paymentId);
            }
            throw translateInstantPaymentException(e);
        } catch (Exception e) { // ResourceAccessException 대신 일반 Exception 처리
            log.error("Payment instant request unexpected error. paymentId={}, durationMs={}, type={}, message={}",
                    paymentId, System.currentTimeMillis() - startedAt, e.getClass().getName(), e.getMessage(), e);
            throw new GeneralException(GeneralErrorCode.EXTERNAL_SERVICE_TIMEOUT, "결제 서버 연결에 실패했습니다.");
        }
    }

    public PaymentResponse cancelPayment(String paymentId) {
        try {
            return paymentFeignClient.cancelPayment(paymentId);
        } catch (FeignException e) {
            log.warn("Payment cancel request failed. status={}, body={}", e.status(), safeBody(e));
            throw translateCancelPaymentException(e);
        } catch (Exception e) {
            log.warn("Payment cancel request unexpected error. type={}, message={}",
                    e.getClass().getName(), e.getMessage());
            throw new GeneralException(GeneralErrorCode.EXTERNAL_SERVICE_TIMEOUT, "결제 서버 연결에 실패했습니다.");
        }
    }

    public PaymentResponse getPayment(String paymentId) {
        long startedAt = System.currentTimeMillis();
        try {
            PaymentResponse response = paymentFeignClient.getPayment(paymentId);
            log.info("Payment get request completed. paymentId={}, paymentStatus={}, durationMs={}",
                    paymentId,
                    response != null && response.data() != null ? response.data().paymentStatus() : "null",
                    System.currentTimeMillis() - startedAt);
            return response;
        } catch (FeignException e) {
            log.warn("Payment get request failed. paymentId={}, status={}, durationMs={}, body={}",
                    paymentId, e.status(), System.currentTimeMillis() - startedAt, safeBody(e));
            throw new GeneralException(GeneralErrorCode.PAYMENT_FAILED, "결제 내역 조회에 실패했습니다. status=" + e.status());
        } catch (Exception e) {
            log.error("Payment get request unexpected error. paymentId={}, durationMs={}, type={}, message={}",
                    paymentId, System.currentTimeMillis() - startedAt, e.getClass().getName(), e.getMessage(), e);
            throw new GeneralException(GeneralErrorCode.EXTERNAL_SERVICE_TIMEOUT, "결제 서버 연결에 실패했습니다.");
        }
    }

    private String safeBody(FeignException e) {
        try {
            String body = e.contentUTF8();
            return (body == null || body.isBlank()) ? "<empty>" : body;
        } catch (Exception ignored) {
            return "<unavailable>";
        }
    }

    private boolean isEmptySuccessResponse(FeignException e) {
        return e.status() == 200 && "<empty>".equals(safeBody(e));
    }

    private GeneralException translateInstantPaymentException(FeignException e) {
        return switch (e.status()) {
            case 403 -> new GeneralException(GeneralErrorCode.PAYMENT_FAILED, "storeId와 API Secret이 일치하지 않습니다.");
            case 404 -> new GeneralException(GeneralErrorCode.PAYMENT_FAILED, "결제 서버에 등록된 가맹점이 아닙니다.");
            case 409 -> new GeneralException(GeneralErrorCode.PAYMENT_ALREADY_PROCESSED, "중복된 paymentId 요청입니다.");
            case 500 -> new GeneralException(GeneralErrorCode.PAYMENT_FAILED, "결제 서버 랜덤 실패가 발생했습니다.");
            default -> new GeneralException(GeneralErrorCode.PAYMENT_SERVER_FAILED, "결제 서버 응답 오류입니다. status=" + e.status());
        };
    }

    private GeneralException translateCancelPaymentException(FeignException e) {
        return switch (e.status()) {
            case 404 -> new GeneralException(GeneralErrorCode.PAYMENT_FAILED, "취소할 결제 내역을 찾지 못했습니다.");
            case 409 -> new GeneralException(GeneralErrorCode.PAYMENT_NOT_CANCELLABLE, "이미 취소되었거나 취소할 수 없는 결제입니다.");
            default -> new GeneralException(GeneralErrorCode.PAYMENT_SERVER_FAILED, "결제 취소 응답 오류입니다. status=" + e.status());
        };
    }
}
