import http from 'k6/http';
import { check, sleep } from 'k6';

// 1. 서버 주소 설정
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';

export const options = {
    stages: [
        { duration: '10s', target: 30 },  // 10초 동안 30명까지 서서히 증가
        { duration: '1m', target: 30 },   // 1분 동안 30명 유지 (안정적인 데이터 확보 구간)
        { duration: '10s', target: 0 },   // 10초 동안 종료
    ],
};

export function setup() {
    return {
        token: " ",
        scheduleId: 1
    };
}

export default function (data) {
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${data.token}`
    };

    // [1] 랜덤 좌석 선택
    const rows = ['A', 'B', 'C', 'D', 'E'];
    const randomRow = rows[Math.floor(Math.random() * rows.length)];
    const randomCol = Math.floor(Math.random() * 10) + 1;

    const reservePayload = JSON.stringify({
        scheduleId: data.scheduleId,
        seats: [{ row: randomRow, column: randomCol }]
    });

    // [2] 예약 요청 실행
    const reserveRes = http.post(`${BASE_URL}/reservations`, reservePayload, { headers });

    // 예약 결과 체크
    const isReserveSuccess = check(reserveRes, {
        'reservation status 200': (r) => r.status === 200,
        'reservation status 409 (expected)': (r) => r.status === 409,
    });

    // [3] 예약 성공 시에만 결제 진행
    if (reserveRes.status === 200) {
        const responseBody = JSON.parse(reserveRes.body);
        const reservationId = responseBody.data.reservationId;

        const paymentPayload = JSON.stringify({
            storeId: "Seungwon326",
            orderName: "CGV 영화 예매 테스트",
            totalPayAmount: 15000,
            category: "MOVIE",
            targetId: reservationId,
            currency: "KRW"
        });

        const payRes = http.post(`${BASE_URL}/payment`, paymentPayload, { headers });

        // 결제 결과 체크
        check(payRes, {
            'payment successful': (r) => r.status === 200,
        });
    }

    // [4] 서버 부하 조절을 위한 생각 시간 (1초)
    sleep(1);
}