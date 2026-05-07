import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

export const options = {
    stages: [
        { duration: '1m', target: 10 },
        { duration: '2m', target: 30 },
        { duration: '1m', target: 0 },
    ],
    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<1000'],
        reservation_duration: ['p(95)<800'],
        payment_duration: ['p(95)<1000'],
        reservation_success_rate: ['rate>0.95'],
        payment_success_rate: ['rate>0.95'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://13.125.8.199:8080';
const MOVIE_SCREEN_ID = Number(__ENV.MOVIE_SCREEN_ID || 1);

// 서로 다른 좌석 목록을 쉼표로 전달
// 예: k6 run -e SEAT_IDS="1,2,3,4,5,6,7,8,9,10" reserveK6UniqueSeats.js
const SEAT_IDS = (__ENV.SEAT_IDS || '1,2,3,4,5,6,7,8,9,10')
    .split(',')
    .map((seatId) => Number(seatId.trim()))
    .filter((seatId) => Number.isInteger(seatId) && seatId > 0);

const TOKENS = (__ENV.TOKENS || '')
    .split(',')
    .map((token) => token.trim())
    .filter(Boolean);

const reservationSuccess = new Counter('reservation_success');
const reservationFail = new Counter('reservation_fail');
const paymentSuccess = new Counter('payment_success');
const paymentFail = new Counter('payment_fail');

const reservationSuccessRate = new Rate('reservation_success_rate');
const paymentSuccessRate = new Rate('payment_success_rate');
const reservationDuration = new Trend('reservation_duration');
const paymentDuration = new Trend('payment_duration');

if (SEAT_IDS.length === 0) {
    throw new Error('SEAT_IDS is empty. Pass seat ids with -e SEAT_IDS="1,2,3"');
}

if (TOKENS.length === 0) {
    throw new Error('TOKENS is empty. Pass user JWTs with -e TOKENS="token1,token2"');
}

export default function () {
    const token = TOKENS[(__VU - 1) % TOKENS.length];
    const seatId = SEAT_IDS[(__ITER + __VU - 1) % SEAT_IDS.length];

    const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
    };

    const payload = JSON.stringify({
        movieScreenId: MOVIE_SCREEN_ID,
        seatIds: [seatId],
    });

    const resCreate = http.post(`${BASE_URL}/api/reservations`, payload, {
        headers,
        tags: { api: 'create_reservation', test_type: 'unique_seats' },
    });

    reservationDuration.add(resCreate.timings.duration);

    const reservationOk = check(resCreate, {
        'reservation success': (r) => r.status === 200 || r.status === 201,
    });

    reservationSuccessRate.add(reservationOk);

    if (!reservationOk) {
        reservationFail.add(1);
        sleep(1);
        return;
    }

    reservationSuccess.add(1);

    let reservationId;
    try {
        reservationId = resCreate.json().payload;
    } catch (e) {
        reservationFail.add(1);
        sleep(1);
        return;
    }

    const resPayment = http.post(
        `${BASE_URL}/api/reservations/${reservationId}/payments`,
        null,
        {
            headers,
            tags: { api: 'create_payment', test_type: 'unique_seats' },
        }
    );

    paymentDuration.add(resPayment.timings.duration);

    const paymentOk = check(resPayment, {
        'payment success': (r) => r.status === 200 || r.status === 201,
    });

    paymentSuccessRate.add(paymentOk);

    if (paymentOk) {
        paymentSuccess.add(1);
    } else {
        paymentFail.add(1);
    }

    sleep(1);
}
