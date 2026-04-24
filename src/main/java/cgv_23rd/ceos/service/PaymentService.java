package cgv_23rd.ceos.service;

import cgv_23rd.ceos.dto.payment.request.InstantPaymentRequest;
import cgv_23rd.ceos.dto.payment.response.PaymentResponse;
import cgv_23rd.ceos.global.config.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProperties paymentProperties;
    private final RestTemplate paymentRestTemplate;

    // 결제 서버로 결제 요청을 보내고 결과 반환
    public PaymentResponse requestInstantPayment(String paymentId, String orderName, int amount) {
        InstantPaymentRequest request = new InstantPaymentRequest(
                paymentProperties.storeId(),
                orderName,
                amount,
                "KRW", // 필수 값
                null   // 커스텀 데이터 생략 가능
        );

        return paymentRestTemplate.postForObject(
                "/payments/{paymentId}/instant", // 실제 API 경로
                request,
                PaymentResponse.class,
                paymentId // Path Parameter 바인딩
        );
    }
}