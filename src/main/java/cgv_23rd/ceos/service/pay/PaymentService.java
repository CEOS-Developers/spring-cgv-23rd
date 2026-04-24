package cgv_23rd.ceos.service.pay;

import cgv_23rd.ceos.dto.payment.request.InstantPaymentRequest;
import cgv_23rd.ceos.dto.payment.response.PaymentResponse;
import cgv_23rd.ceos.global.apiPayload.code.GeneralErrorCode;
import cgv_23rd.ceos.global.apiPayload.exception.GeneralException;
import cgv_23rd.ceos.global.config.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProperties paymentProperties;
    private final RestTemplate paymentRestTemplate;

    // 결제 서버로 결제 요청을 보내고 결과 반환
    public PaymentResponse requestInstantPayment(String paymentId, String orderName, int amount, String customData) {
        InstantPaymentRequest request = new InstantPaymentRequest(
                paymentProperties.storeId(),
                orderName,
                amount,
                "KRW", // 필수 값
                customData
        );

        try {
            return paymentRestTemplate.postForObject(
                    "/payments/{paymentId}/instant",
                    request,
                    PaymentResponse.class,
                    paymentId
            );
        } catch (HttpStatusCodeException e) {
            throw translateInstantPaymentException(e);
        } catch (ResourceAccessException e) {
            throw new GeneralException(GeneralErrorCode.EXTERNAL_SERVICE_TIMEOUT, "결제 서버 연결에 실패했습니다.");
        }
    }

    public PaymentResponse cancelPayment(String paymentId) {
        try {
            return paymentRestTemplate.postForObject(
                    "/payments/{paymentId}/cancel",
                    null,
                    PaymentResponse.class,
                    paymentId
            );
        } catch (HttpStatusCodeException e) {
            throw translateCancelPaymentException(e);
        } catch (ResourceAccessException e) {
            throw new GeneralException(GeneralErrorCode.EXTERNAL_SERVICE_TIMEOUT, "결제 서버 연결에 실패했습니다.");
        }
    }

    public PaymentResponse getPayment(String paymentId) {
        try {
            return paymentRestTemplate.getForObject(
                    "/payments/{paymentId}",
                    PaymentResponse.class,
                    paymentId
            );
        } catch (HttpStatusCodeException e) {
            throw new GeneralException(GeneralErrorCode.PAYMENT_FAILED, "결제 내역 조회에 실패했습니다. status=" + e.getStatusCode().value());
        } catch (ResourceAccessException e) {
            throw new GeneralException(GeneralErrorCode.EXTERNAL_SERVICE_TIMEOUT, "결제 서버 연결에 실패했습니다.");
        }
    }

    private GeneralException translateInstantPaymentException(HttpStatusCodeException e) {
        return switch (e.getStatusCode().value()) {
            case 403 -> new GeneralException(GeneralErrorCode.PAYMENT_FAILED, "storeId와 API Secret이 일치하지 않습니다.");
            case 404 -> new GeneralException(GeneralErrorCode.PAYMENT_FAILED, "결제 서버에 등록된 가맹점이 아닙니다.");
            case 409 -> new GeneralException(GeneralErrorCode.PAYMENT_ALREADY_PROCESSED, "중복된 paymentId 요청입니다.");
            case 500 -> new GeneralException(GeneralErrorCode.PAYMENT_FAILED, "결제 서버 랜덤 실패가 발생했습니다.");
            default -> new GeneralException(GeneralErrorCode.PAYMENT_SERVER_FAILED, "결제 서버 응답 오류입니다. status=" + e.getStatusCode().value());
        };
    }

    private GeneralException translateCancelPaymentException(HttpStatusCodeException e) {
        return switch (e.getStatusCode().value()) {
            case 404 -> new GeneralException(GeneralErrorCode.PAYMENT_FAILED, "취소할 결제 내역을 찾지 못했습니다.");
            case 409 -> new GeneralException(GeneralErrorCode.PAYMENT_NOT_CANCELLABLE, "이미 취소되었거나 취소할 수 없는 결제입니다.");
            default -> new GeneralException(GeneralErrorCode.PAYMENT_SERVER_FAILED, "결제 취소 응답 오류입니다. status=" + e.getStatusCode().value());
        };
    }
}
