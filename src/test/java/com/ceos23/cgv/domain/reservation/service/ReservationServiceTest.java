package com.ceos23.cgv.domain.reservation.service;

import com.ceos23.cgv.domain.cinema.entity.Theater;
import com.ceos23.cgv.domain.cinema.enums.TheaterType;
import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.entity.Screening;
import com.ceos23.cgv.domain.movie.repository.ScreeningRepository;
import com.ceos23.cgv.domain.payment.service.PaymentService;
import com.ceos23.cgv.domain.reservation.dto.ReservedSeatRequest;
import com.ceos23.cgv.domain.reservation.entity.Reservation;
import com.ceos23.cgv.domain.reservation.enums.Payment;
import com.ceos23.cgv.domain.reservation.enums.ReservationStatus;
import com.ceos23.cgv.domain.reservation.repository.ReservationRepository;
import com.ceos23.cgv.domain.reservation.repository.ReservedSeatRepository;
import com.ceos23.cgv.domain.user.entity.User;
import com.ceos23.cgv.domain.user.repository.UserRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final String PAYMENT_ID = "reservation-payment-id";

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservedSeatRepository reservedSeatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private PlatformTransactionManager transactionManager;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUpTransactionManager() {
        given(transactionManager.getTransaction(any(TransactionDefinition.class)))
                .willReturn(new SimpleTransactionStatus());
    }

    @Test
    @DisplayName("예매 생성 시 좌석 선점 후 외부 결제가 성공하면 예매 상태가 COMPLETED가 된다")
    void createReservation_Success() {
        // Given
        Long userId = 1L;
        Long screeningId = 1L;
        User user = User.builder().id(userId).nickname("우혁").build();
        Screening screening = createScreening(screeningId, TheaterType.NORMAL);
        AtomicReference<Reservation> savedReservation = new AtomicReference<>();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(screeningRepository.findByIdForUpdate(screeningId)).willReturn(Optional.of(screening));
        given(paymentService.createPaymentId()).willReturn(PAYMENT_ID);
        given(reservationRepository.save(any(Reservation.class))).willAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            savedReservation.set(reservation);
            return reservation;
        });
        given(reservationRepository.findByPaymentId(PAYMENT_ID)).willAnswer(invocation ->
                Optional.of(savedReservation.get()));

        // When
        Reservation reservation = reservationService.createReservation(
                userId,
                screeningId,
                2,
                Payment.APP_CARD,
                null,
                seats()
        );

        // Then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
        assertThat(reservation.getPaymentId()).isEqualTo(PAYMENT_ID);
        verify(reservedSeatRepository).saveAll(anyList());
        verify(paymentService).requestInstantPayment(savedReservation.get());
    }

    @Test
    @DisplayName("결제 실패 시 PENDING 예매를 취소하고 선점 좌석을 복구한다")
    void createReservation_Fail_PaymentFailed() {
        // Given
        Long userId = 1L;
        Long screeningId = 1L;
        User user = User.builder().id(userId).nickname("우혁").build();
        Screening screening = createScreening(screeningId, TheaterType.NORMAL);
        AtomicReference<Reservation> savedReservation = new AtomicReference<>();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(screeningRepository.findByIdForUpdate(screeningId)).willReturn(Optional.of(screening));
        given(paymentService.createPaymentId()).willReturn(PAYMENT_ID);
        given(reservationRepository.save(any(Reservation.class))).willAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            savedReservation.set(reservation);
            return reservation;
        });
        given(reservationRepository.findByPaymentId(PAYMENT_ID)).willAnswer(invocation ->
                Optional.of(savedReservation.get()));
        given(paymentService.requestInstantPayment(any(Reservation.class)))
                .willThrow(new CustomException(ErrorCode.PAYMENT_FAILED));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> reservationService.createReservation(
                userId,
                screeningId,
                2,
                Payment.APP_CARD,
                null,
                seats()
        ));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_FAILED);
        assertThat(savedReservation.get().getStatus()).isEqualTo(ReservationStatus.CANCELED);
        verify(reservedSeatRepository).deleteAllByReservationId(savedReservation.get().getId());
    }

    @Test
    @DisplayName("좌석 저장 중 중복 좌석이 감지되면 결제를 요청하지 않고 SEAT_ALREADY_RESERVED 예외가 발생한다")
    void createReservation_Fail_AlreadyReservedSeat() {
        // Given
        Long userId = 1L;
        Long screeningId = 1L;
        User user = User.builder().id(userId).nickname("우혁").build();
        Screening screening = createScreening(screeningId, TheaterType.NORMAL);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(screeningRepository.findByIdForUpdate(screeningId)).willReturn(Optional.of(screening));
        given(paymentService.createPaymentId()).willReturn(PAYMENT_ID);
        given(reservationRepository.save(any(Reservation.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(reservedSeatRepository.saveAll(anyList())).willThrow(DataIntegrityViolationException.class);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> reservationService.createReservation(
                userId,
                screeningId,
                2,
                Payment.APP_CARD,
                null,
                seats()
        ));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SEAT_ALREADY_RESERVED);
        verify(paymentService, never()).requestInstantPayment(any(Reservation.class));
    }

    @Test
    @DisplayName("결제 완료된 예매를 취소하면 외부 결제 취소 후 내부 예매와 좌석을 취소한다")
    void cancelReservation_Success() {
        // Given
        Long userId = 1L;
        Long reservationId = 1L;
        User user = User.builder().id(userId).build();
        Reservation reservation = createCompletedReservation(reservationId, user);

        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

        // When
        reservationService.cancelReservation(userId, reservationId);

        // Then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
        verify(paymentService).cancelPayment(PAYMENT_ID);
        verify(reservedSeatRepository).deleteAllByReservationId(reservationId);
    }

    @Test
    @DisplayName("존재하지 않는 상영일정으로 예매 시 SCREENING_NOT_FOUND 예외가 발생한다")
    void createReservation_Fail_ScreeningNotFound() {
        // Given
        Long userId = 1L;
        Long invalidScreeningId = 999L;
        User user = User.builder().id(userId).nickname("우혁").build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(screeningRepository.findByIdForUpdate(invalidScreeningId)).willReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> reservationService.createReservation(
                userId,
                invalidScreeningId,
                2,
                Payment.APP_CARD,
                null,
                seats()
        ));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SCREENING_NOT_FOUND);
    }

    private Reservation createCompletedReservation(Long reservationId, User user) {
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .user(user)
                .screening(createScreening(1L, TheaterType.NORMAL))
                .status(ReservationStatus.PENDING)
                .peopleCount(2)
                .price(30000)
                .payment(Payment.APP_CARD)
                .saleNumber("sale-number")
                .paymentId(PAYMENT_ID)
                .build();
        reservation.completePayment();
        return reservation;
    }

    private Screening createScreening(Long screeningId, TheaterType theaterType) {
        Movie movie = Movie.builder().id(1L).title("테스트 영화").build();
        Theater theater = Theater.builder().id(1L).name("1관").type(theaterType).build();

        return Screening.builder()
                .id(screeningId)
                .movie(movie)
                .theater(theater)
                .isMorning(false)
                .build();
    }

    private List<ReservedSeatRequest.SeatInfo> seats() {
        return List.of(
                new ReservedSeatRequest.SeatInfo("G", 4),
                new ReservedSeatRequest.SeatInfo("G", 5)
        );
    }
}
