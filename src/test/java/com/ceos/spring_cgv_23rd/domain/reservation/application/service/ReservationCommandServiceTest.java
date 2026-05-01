package com.ceos.spring_cgv_23rd.domain.reservation.application.service;

import com.ceos.spring_cgv_23rd.domain.payment.application.dto.command.PayCommand;
import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PaymentResult;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.CancelPaymentUseCase;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.PaymentUseCase;
import com.ceos.spring_cgv_23rd.domain.payment.domain.PaymentStatus;
import com.ceos.spring_cgv_23rd.domain.payment.exception.PaymentErrorCode;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.ConfirmReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.command.CreateReservationCommand;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationDetailResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ReservationResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.ScreeningInfoResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result.SeatInfoResult;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ReservationPersistencePort;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ScreeningPort;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.SeatHoldPort;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.SeatPort;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.ReservationStatus;
import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class ReservationCommandServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long SCREENING_ID = 100L;
    private static final List<Long> SEAT_IDS = List.of(10L, 11L);
    private static final Long HALL_TYPE_ID = 5L;
    private static final int SCREENING_PRICE = 15000;
    private static final String PAYMENT_ID = "test-paymentId-uuid";


    @InjectMocks
    private ReservationCommandService reservationCommandService;
    @Mock
    private ReservationPersistencePort reservationPersistencePort;


    @Mock
    private ScreeningPort screeningPort;
    @Mock
    private SeatPort seatPort;
    @Mock
    private SeatHoldPort seatHoldPort;
    @Mock
    private ReservationTxService reservationTxService;
    @Mock
    private PaymentUseCase paymentUseCase;
    @Mock
    private CancelPaymentUseCase cancelPaymentUseCase;


    private ScreeningInfoResult defaultScreeningInfo() {
        return new ScreeningInfoResult(
                HALL_TYPE_ID, "인터스텔라", "CGV 강남", "1관",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                SCREENING_PRICE
        );
    }

    private Map<Long, SeatInfoResult> defaultSeatInfoMap() {
        return Map.of(
                SEAT_IDS.getFirst(), new SeatInfoResult(SEAT_IDS.getFirst(), 1, 5),
                SEAT_IDS.get(1), new SeatInfoResult(SEAT_IDS.get(1), 1, 6)
        );
    }

    private Reservation savedReservation() {
        return Reservation.restore(
                1L, USER_ID, null, SCREENING_ID,
                "260310-ABCD1234", PAYMENT_ID,
                ReservationStatus.COMPLETED,
                SCREENING_PRICE * SEAT_IDS.size(),
                SEAT_IDS,
                LocalDateTime.now()
        );
    }

    private PaymentResult defaultPaymentResult() {
        return new PaymentResult(
                PAYMENT_ID, PaymentStatus.PAID,
                SCREENING_PRICE * SEAT_IDS.size(),
                "인터스텔라 2석", "toss", LocalDateTime.now()
        );
    }


    /**
     * createReservation
     */

    @Nested
    @DisplayName("createReservation")
    class CreateReservation {

        @Test
        @DisplayName("정상 흐름")
        void success() {
            // given
            CreateReservationCommand command = new CreateReservationCommand(SCREENING_ID, SEAT_IDS);

            ScreeningInfoResult screeningInfo = defaultScreeningInfo();
            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.of(screeningInfo));
            given(seatPort.findSeatInfoByIdsAndHallTypeId(SEAT_IDS, HALL_TYPE_ID)).willReturn(defaultSeatInfoMap());
            given(reservationPersistencePort.findReservedSeatIdsByScreeningId(SCREENING_ID)).willReturn(List.of());
            given(seatHoldPort.holdSeats(eq(SCREENING_ID), eq(SEAT_IDS), anyString(), anyLong())).willReturn(true);

            // when
            ReservationResult result = reservationCommandService.createReservation(USER_ID, command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.screeningId()).isEqualTo(SCREENING_ID);
            assertThat(result.seatIds()).isEqualTo(SEAT_IDS);
            assertThat(result.expiresAt()).isAfter(LocalDateTime.now());
        }

        @Test
        @DisplayName("존재하지 않는 상영 -> SCREENING_NOT_FOUND")
        void screeningNotFound() {
            // given
            CreateReservationCommand command = new CreateReservationCommand(SCREENING_ID, SEAT_IDS);
            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reservationCommandService.createReservation(USER_ID, command))
                    .isInstanceOf(GeneralException.class)
                    .asInstanceOf(type(GeneralException.class))
                    .extracting(GeneralException::getErrorCode)
                    .isEqualTo(ReservationErrorCode.SCREENING_NOT_FOUND);
        }

        @Test
        @DisplayName("존재하지 않는 좌석 포함 -> SEAT_NOT_FOUND")
        void seatNotFound() {
            // given
            CreateReservationCommand command = new CreateReservationCommand(SCREENING_ID, SEAT_IDS);

            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.of(defaultScreeningInfo()));
            // 좌석 2개 요청했는데 1개만 존재
            given(seatPort.findSeatInfoByIdsAndHallTypeId(SEAT_IDS, HALL_TYPE_ID))
                    .willReturn(Map.of(SEAT_IDS.getFirst(), new SeatInfoResult(SEAT_IDS.getFirst(), 1, 5)));

            // when & then
            assertThatThrownBy(() -> reservationCommandService.createReservation(USER_ID, command))
                    .isInstanceOf(GeneralException.class)
                    .asInstanceOf(type(GeneralException.class))
                    .extracting(GeneralException::getErrorCode)
                    .isEqualTo(ReservationErrorCode.SEAT_NOT_FOUND);
        }

        @Test
        @DisplayName("이미 예약된 좌석 -> SEAT_ALREADY_RESERVED")
        void seatAlreadyReserved() {
            // given
            CreateReservationCommand command = new CreateReservationCommand(SCREENING_ID, SEAT_IDS);

            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.of(defaultScreeningInfo()));
            given(seatPort.findSeatInfoByIdsAndHallTypeId(SEAT_IDS, HALL_TYPE_ID)).willReturn(defaultSeatInfoMap());
            // seatId=SEAT_IDS.getFirst()가 이미 예약됨
            given(reservationPersistencePort.findReservedSeatIdsByScreeningId(SCREENING_ID)).willReturn(List.of(SEAT_IDS.getFirst()));

            // when & then
            assertThatThrownBy(() -> reservationCommandService.createReservation(USER_ID, command))
                    .isInstanceOf(GeneralException.class)
                    .asInstanceOf(type(GeneralException.class))
                    .extracting(GeneralException::getErrorCode)
                    .isEqualTo(ReservationErrorCode.SEAT_ALREADY_RESERVED);
        }

        @Test
        @DisplayName("좌석 Hold 실패 (다른 사용자가 선점) -> SEAT_ALREADY_RESERVED")
        void holdFailed() {
            // given
            CreateReservationCommand command = new CreateReservationCommand(SCREENING_ID, SEAT_IDS);

            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.of(defaultScreeningInfo()));
            given(seatPort.findSeatInfoByIdsAndHallTypeId(SEAT_IDS, HALL_TYPE_ID)).willReturn(defaultSeatInfoMap());
            given(reservationPersistencePort.findReservedSeatIdsByScreeningId(SCREENING_ID)).willReturn(List.of());
            given(seatHoldPort.holdSeats(eq(SCREENING_ID), eq(SEAT_IDS), anyString(), anyLong())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> reservationCommandService.createReservation(USER_ID, command))
                    .isInstanceOf(GeneralException.class)
                    .asInstanceOf(type(GeneralException.class))
                    .extracting(GeneralException::getErrorCode)
                    .isEqualTo(ReservationErrorCode.SEAT_ALREADY_RESERVED);
        }
    }


    /**
     * confirmReservation
     */

    @Nested
    @DisplayName("confirmReservation")
    class ConfirmReservation {

        private ConfirmReservationCommand defaultCommand() {
            return new ConfirmReservationCommand(PAYMENT_ID, USER_ID, SCREENING_ID, SEAT_IDS);
        }

        private String holderKey() {
            return "user:" + USER_ID;
        }

        @Test
        @DisplayName("정상 흐름")
        void success() {
            // given
            ConfirmReservationCommand command = defaultCommand();
            ScreeningInfoResult screeningInfo = defaultScreeningInfo();
            Map<Long, SeatInfoResult> seatInfoMap = defaultSeatInfoMap();
            Reservation reservation = savedReservation();
            PaymentResult paymentResult = defaultPaymentResult();

            given(seatHoldPort.isHeldByUser(SCREENING_ID, SEAT_IDS, holderKey())).willReturn(true);
            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.of(screeningInfo));
            given(seatPort.findSeatInfoByIdsAndHallTypeId(SEAT_IDS, HALL_TYPE_ID)).willReturn(seatInfoMap);
            given(paymentUseCase.pay(any(PayCommand.class))).willReturn(paymentResult);
            given(reservationTxService.persistConfirmed(
                    USER_ID, SCREENING_ID, SCREENING_PRICE, PAYMENT_ID, SEAT_IDS))
                    .willReturn(reservation);

            // when
            ReservationDetailResult result = reservationCommandService.confirmReservation(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.reservationId()).isEqualTo(1L);
            assertThat(result.status()).isEqualTo(ReservationStatus.COMPLETED);
            assertThat(result.movieTitle()).isEqualTo("인터스텔라");
            assertThat(result.totalPrice()).isEqualTo(SCREENING_PRICE * SEAT_IDS.size());
            assertThat(result.seats()).hasSize(2);
            assertThat(result.payment()).isEqualTo(paymentResult);

            // Hold 해제 호출 검증
            then(seatHoldPort).should().releaseSeats(SCREENING_ID, SEAT_IDS);
        }

        @Test
        @DisplayName("좌석 Hold 만료 -> SEAT_HOLD_EXPIRED")
        void holdExpired() {
            // given
            ConfirmReservationCommand command = defaultCommand();
            given(seatHoldPort.isHeldByUser(SCREENING_ID, SEAT_IDS, holderKey())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> reservationCommandService.confirmReservation(command))
                    .isInstanceOf(GeneralException.class)
                    .asInstanceOf(type(GeneralException.class))
                    .extracting(GeneralException::getErrorCode)
                    .isEqualTo(ReservationErrorCode.SEAT_HOLD_EXPIRED);


            // PG 결제 호출 없음
            then(paymentUseCase).should(never()).pay(any());
        }

        @Test
        @DisplayName("PG 결제 실패 -> 예외 전파, persistConfirmed 호출 없음")
        void paymentFailed() {
            // given
            ConfirmReservationCommand command = defaultCommand();
            ScreeningInfoResult screeningInfo = defaultScreeningInfo();

            given(seatHoldPort.isHeldByUser(SCREENING_ID, SEAT_IDS, holderKey())).willReturn(true);
            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.of(screeningInfo));
            given(seatPort.findSeatInfoByIdsAndHallTypeId(SEAT_IDS, HALL_TYPE_ID)).willReturn(defaultSeatInfoMap());
            given(paymentUseCase.pay(any(PayCommand.class))).willThrow(new GeneralException(PaymentErrorCode.PG_CALL_FAILED));

            // when & then
            assertThatThrownBy(() -> reservationCommandService.confirmReservation(command))
                    .isInstanceOf(GeneralException.class);

            // 예매 저장 호출 없음
            then(reservationTxService).should(never())
                    .persistConfirmed(anyLong(), anyLong(), anyInt(), anyString(), anyList());
        }

        @Test
        @DisplayName("persistConfirmed 실패 → 보상 결제 취소 + CONFIRM_FAILED_ROLLED_BACK")
        void persistFailed_compensatesPayment() {
            // given
            ConfirmReservationCommand command = defaultCommand();
            ScreeningInfoResult screeningInfo = defaultScreeningInfo();

            given(seatHoldPort.isHeldByUser(SCREENING_ID, SEAT_IDS, holderKey())).willReturn(true);
            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.of(screeningInfo));
            given(seatPort.findSeatInfoByIdsAndHallTypeId(SEAT_IDS, HALL_TYPE_ID)).willReturn(defaultSeatInfoMap());
            given(paymentUseCase.pay(any(PayCommand.class))).willReturn(defaultPaymentResult());
            given(reservationTxService.persistConfirmed(
                    USER_ID, SCREENING_ID, SCREENING_PRICE, PAYMENT_ID, SEAT_IDS))
                    .willThrow(new RuntimeException("DB connection lost"));

            // when & then
            assertThatThrownBy(() -> reservationCommandService.confirmReservation(command))
                    .isInstanceOf(GeneralException.class)
                    .asInstanceOf(type(GeneralException.class))
                    .extracting(GeneralException::getErrorCode)
                    .isEqualTo(ReservationErrorCode.CONFIRM_FAILED_ROLLED_BACK);

            // 보상 결제 취소 호출 검증
            then(cancelPaymentUseCase).should().cancel(PAYMENT_ID);
        }

        @Test
        @DisplayName("persistConfirmed 실패 + 보상 결제 취소도 실패 -> CONFIRM_FAILED_ROLLED_BACK")
        void persistFailed_compensationAlsoFails() {
            // given
            ConfirmReservationCommand command = defaultCommand();
            ScreeningInfoResult screeningInfo = defaultScreeningInfo();

            given(seatHoldPort.isHeldByUser(SCREENING_ID, SEAT_IDS, holderKey())).willReturn(true);
            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.of(screeningInfo));
            given(seatPort.findSeatInfoByIdsAndHallTypeId(SEAT_IDS, HALL_TYPE_ID)).willReturn(defaultSeatInfoMap());
            given(paymentUseCase.pay(any(PayCommand.class))).willReturn(defaultPaymentResult());
            given(reservationTxService.persistConfirmed(
                    USER_ID, SCREENING_ID, SCREENING_PRICE, PAYMENT_ID, SEAT_IDS))
                    .willThrow(new RuntimeException("DB connection lost"));
            doThrow(new RuntimeException("PG also down")).when(cancelPaymentUseCase).cancel(PAYMENT_ID);

            // when & then
            assertThatThrownBy(() -> reservationCommandService.confirmReservation(command))
                    .isInstanceOf(GeneralException.class)
                    .asInstanceOf(type(GeneralException.class))
                    .extracting(GeneralException::getErrorCode)
                    .isEqualTo(ReservationErrorCode.CONFIRM_FAILED_ROLLED_BACK);
        }

        @Test
        @DisplayName("정상 확정 후 Hold 해제 실패 -> 예외 삼키고 정상 결과 반환")
        void holdReleaseFails_stillReturnsResult() {
            // given
            ConfirmReservationCommand command = defaultCommand();
            ScreeningInfoResult screeningInfo = defaultScreeningInfo();
            Map<Long, SeatInfoResult> seatInfoMap = defaultSeatInfoMap();
            Reservation reservation = savedReservation();
            PaymentResult paymentResult = defaultPaymentResult();

            given(seatHoldPort.isHeldByUser(SCREENING_ID, SEAT_IDS, holderKey())).willReturn(true);
            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.of(screeningInfo));
            given(seatPort.findSeatInfoByIdsAndHallTypeId(SEAT_IDS, HALL_TYPE_ID)).willReturn(seatInfoMap);
            given(paymentUseCase.pay(any(PayCommand.class))).willReturn(paymentResult);
            given(reservationTxService.persistConfirmed(
                    USER_ID, SCREENING_ID, SCREENING_PRICE, PAYMENT_ID, SEAT_IDS))
                    .willReturn(reservation);
            doThrow(new RuntimeException("Redis down")).when(seatHoldPort).releaseSeats(SCREENING_ID, SEAT_IDS);

            // when
            ReservationDetailResult result = reservationCommandService.confirmReservation(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.reservationId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("PG 결제 요청 시 올바른 금액과 주문명이 전달되는지 검증")
        void payCommandContainsCorrectAmountAndOrderName() {
            // given
            ConfirmReservationCommand command = defaultCommand();
            ScreeningInfoResult screeningInfo = defaultScreeningInfo();
            Reservation reservation = savedReservation();
            PaymentResult paymentResult = defaultPaymentResult();

            given(seatHoldPort.isHeldByUser(SCREENING_ID, SEAT_IDS, holderKey())).willReturn(true);
            given(screeningPort.findScreeningInfoById(SCREENING_ID)).willReturn(Optional.of(screeningInfo));
            given(seatPort.findSeatInfoByIdsAndHallTypeId(SEAT_IDS, HALL_TYPE_ID)).willReturn(defaultSeatInfoMap());
            given(paymentUseCase.pay(any(PayCommand.class))).willReturn(paymentResult);
            given(reservationTxService.persistConfirmed(
                    USER_ID, SCREENING_ID, SCREENING_PRICE, PAYMENT_ID, SEAT_IDS))
                    .willReturn(reservation);

            // when
            reservationCommandService.confirmReservation(command);

            // then
            then(paymentUseCase).should().pay(argThat(cmd ->
                    cmd.amount() == SCREENING_PRICE * SEAT_IDS.size()
                            && "인터스텔라 2석".equals(cmd.orderName())
                            && PAYMENT_ID.equals(cmd.paymentId())
            ));
        }
    }
}
