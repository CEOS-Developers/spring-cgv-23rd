package com.ceos.spring_cgv_23rd.domain.reservation.application.service;

import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.CancelPaymentUseCase;
import com.ceos.spring_cgv_23rd.domain.payment.exception.PaymentErrorCode;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ReservationPersistencePort;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.ReservationStatus;
import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class ReservationCancelServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long GUEST_ID = 99L;
    private static final Long RESERVATION_ID = 10L;
    private static final Long SCREENING_ID = 100L;
    private static final String PAYMENT_ID = "test-paymentId-uuid";
    private static final List<Long> SEAT_IDS = List.of(10L, 11L);

    @InjectMocks
    private ReservationCancelService reservationCancelService;

    @Mock
    private ReservationPersistencePort reservationPersistencePort;
    @Mock
    private ReservationTxService reservationTxService;
    @Mock
    private CancelPaymentUseCase cancelPaymentUseCase;


    private Reservation completedReservation() {
        return Reservation.restore(
                RESERVATION_ID, USER_ID, null, SCREENING_ID,
                "260310-ABCD1234", PAYMENT_ID,
                ReservationStatus.COMPLETED,
                15000 * SEAT_IDS.size(),
                SEAT_IDS,
                LocalDateTime.now()
        );
    }

    private Reservation guestReservation() {
        return Reservation.restore(
                RESERVATION_ID, null, GUEST_ID, SCREENING_ID,
                "260310-ABCD1234", PAYMENT_ID,
                ReservationStatus.COMPLETED,
                15000 * SEAT_IDS.size(),
                SEAT_IDS,
                LocalDateTime.now()
        );
    }


    @Test
    @DisplayName("정상 흐름")
    void success() {
        // given
        Reservation reservation = completedReservation();
        given(reservationPersistencePort.findReservationWithSeatsById(RESERVATION_ID))
                .willReturn(Optional.of(reservation));

        // when & then
        assertThatCode(() -> reservationCancelService.cancel(USER_ID, RESERVATION_ID))
                .doesNotThrowAnyException();

        // PG 취소 호출 검증
        then(cancelPaymentUseCase).should().cancel(PAYMENT_ID);

        // 예매 취소 + 좌석 복구 호출 검증
        then(reservationTxService).should().applyCancellation(reservation);
    }

    @Test
    @DisplayName("존재하지 않는 예매 -> RESERVATION_NOT_FOUND")
    void reservationNotFound() {
        // given
        given(reservationPersistencePort.findReservationWithSeatsById(RESERVATION_ID))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationCancelService.cancel(USER_ID, RESERVATION_ID))
                .isInstanceOf(GeneralException.class)
                .asInstanceOf(type(GeneralException.class))
                .extracting(GeneralException::getErrorCode)
                .isEqualTo(ReservationErrorCode.RESERVATION_NOT_FOUND);
    }

    @Test
    @DisplayName("다른 사용자의 예매 취소 시도 -> RESERVATION_FORBIDDEN")
    void forbiddenOtherUser() {
        // given
        given(reservationPersistencePort.findReservationWithSeatsById(RESERVATION_ID))
                .willReturn(Optional.of(completedReservation()));

        // when & then
        assertThatThrownBy(() -> reservationCancelService.cancel(OTHER_USER_ID, RESERVATION_ID))
                .isInstanceOf(GeneralException.class)
                .asInstanceOf(type(GeneralException.class))
                .extracting(GeneralException::getErrorCode)
                .isEqualTo(ReservationErrorCode.RESERVATION_FORBIDDEN);

        // PG 취소 호출 없음
        then(cancelPaymentUseCase).should(never()).cancel(PAYMENT_ID);
    }


    @Test
    @DisplayName("PG 취소 실패 -> applyCancellation 호출 없음")
    void pgCancelFailed() {
        // given
        Reservation reservation = completedReservation();
        given(reservationPersistencePort.findReservationWithSeatsById(RESERVATION_ID))
                .willReturn(Optional.of(reservation));
        doThrow(new GeneralException(PaymentErrorCode.PG_CANCEL_FAILED))
                .when(cancelPaymentUseCase).cancel(PAYMENT_ID);

        // when & then
        assertThatThrownBy(() -> reservationCancelService.cancel(USER_ID, RESERVATION_ID))
                .isInstanceOf(GeneralException.class);

        // applyCancellation 호출 없음
        then(reservationTxService).should(never()).applyCancellation(reservation);
    }

    @Test
    @DisplayName("applyCancellation 실패 -> CONFIRM_FAILED_ROLLED_BACK")
    void applyCancellationFailed() {
        // given
        Reservation reservation = completedReservation();
        given(reservationPersistencePort.findReservationWithSeatsById(RESERVATION_ID))
                .willReturn(Optional.of(reservation));
        doThrow(new RuntimeException("DB connection lost"))
                .when(reservationTxService).applyCancellation(reservation);

        // when & then
        assertThatThrownBy(() -> reservationCancelService.cancel(USER_ID, RESERVATION_ID))
                .isInstanceOf(GeneralException.class)
                .asInstanceOf(type(GeneralException.class))
                .extracting(GeneralException::getErrorCode)
                .isEqualTo(ReservationErrorCode.CONFIRM_FAILED_ROLLED_BACK);

        // PG 취소는 호출됨
        then(cancelPaymentUseCase).should().cancel(PAYMENT_ID);
    }
}
