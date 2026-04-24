package cgv_23rd.ceos.service;

import cgv_23rd.ceos.dto.payment.response.PaymentResponse;
import cgv_23rd.ceos.dto.payment.response.PaymentResultDto;
import cgv_23rd.ceos.entity.reservation.Reservation;
import cgv_23rd.ceos.global.apiPayload.code.GeneralErrorCode;
import cgv_23rd.ceos.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationPaymentFacade {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @Transactional
    public PaymentResultDto processPayment(Long reservationId) {
        // 예약 정보 조회
        Reservation reservation = reservationService.getReservation(reservationId);

        // 결제 서버에 넘길 고유 paymentId 및 주문명 생성
        String paymentId = "RES_" + reservationId + "_" + UUID.randomUUID().toString().substring(0, 8);
        String orderName = reservation.getMovieTitle() + " 예매";

        try {
            // 외부 결제 서버 API 호출
            PaymentResponse response = paymentService.requestInstantPayment(
                    paymentId,
                    orderName,
                    reservation.getTotalPrice()
            );

            // 결제 성공 검증: response.data().paymentStatus() 가 "PAID" 인지 확인
            if (response != null && response.data() != null && "PAID".equals(response.data().paymentStatus())) {
                reservationService.confirmReservation(reservation);
                return new PaymentResultDto(true, "결제가 완료되었습니다.");
            } else {
                throw new GeneralException(GeneralErrorCode.PAYMENT_SERVER_FAILED); // 500 랜덤 실패 등 거절 처리
            }

        } catch (Exception e) {
            // 결제 실패(잔액 부족, 랜덤 10% 실패 등) 또는 네트워크 예외 발생 시 좌석 선점 롤백
            reservationService.cancelReservation(reservation);
            throw new GeneralException(GeneralErrorCode.PAYMENT_FAILED);
        }
    }
}