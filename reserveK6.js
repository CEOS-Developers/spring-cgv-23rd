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
        reservation_server_error_rate: ['rate<0.01'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://13.125.8.199:8080';

// 여러 사용자 JWT를 쉼표로 넣어서 사용
const TOKENS = (__ENV.TOKENS || 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHJpbmciLCJpYXQiOjE3NzgwNDgwNzMsImV4cCI6MTc3ODA1MTY3MywidXNlcklkIjo0fQ.x-NK0x95dBiGHvbNI5xujlsSNIwzX_UIihvSm8qlpDI,eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHJpbmcxIiwiaWF0IjoxNzc4MDQ4MTA0LCJleHAiOjE3NzgwNTE3MDQsInVzZXJJZCI6NX0.pyuYSqrkmy68j3MfVMfehAdNNlfIPbBTYtJY_Pp8RjI,eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHJpbmcyIiwiaWF0IjoxNzc4MDQ4MTI0LCJleHAiOjE3NzgwNTE3MjQsInVzZXJJZCI6Nn0.NXH737RJpz3R9krL69IQWuk6kZch-8llGZliyOpqCdc').split(',');

const reservationSuccess = new Counter('reservation_success');
const reservationConflict = new Counter('reservation_conflict');
const reservationServerError = new Counter('reservation_server_error');
const paymentSuccess = new Counter('payment_success');
const paymentFail = new Counter('payment_fail');

const reservationServerErrorRate = new Rate('reservation_server_error_rate');
const reservationDuration = new Trend('reservation_duration');
const paymentDuration = new Trend('payment_duration');

export default function () {
    const token = TOKENS[__VU % TOKENS.length];

    const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
    };

    const payload = JSON.stringify({
        scheduleId: 1,
        seatIds: [1], // 모든 VU가 같은 좌석 요청
    });

    const resCreate = http.post(`${BASE_URL}/api/reservations`, payload, {
        headers,
        tags: { api: 'create_reservation' },
    });

    reservationDuration.add(resCreate.timings.duration);

    check(resCreate, {
        'reservation status is expected': (r) => [200, 201, 409].includes(r.status),
    });

    if (resCreate.status === 200 || resCreate.status === 201) {
        reservationSuccess.add(1);

        let reservationId;
        try {
            reservationId = resCreate.json().payload;
        } catch (e) {
            reservationServerError.add(1);
            reservationServerErrorRate.add(1);
            return;
        }

        const resPayment = http.post(
            `${BASE_URL}/api/reservations/${reservationId}/payments`,
            null,
            {
                headers,
                tags: { api: 'create_payment' },
            }
        );

        paymentDuration.add(resPayment.timings.duration);

        const paymentOk = check(resPayment, {
            'payment success': (r) => r.status === 200 || r.status === 201,
        });

        if (paymentOk) {
            paymentSuccess.add(1);
        } else {
            paymentFail.add(1);
        }
    } else if (resCreate.status === 409) {
        reservationConflict.add(1);
        reservationServerErrorRate.add(0);
    } else if (resCreate.status >= 500) {
        reservationServerError.add(1);
        reservationServerErrorRate.add(1);
    } else {
        reservationServerErrorRate.add(0);
    }

    sleep(1);
}
