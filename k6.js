import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:80';

export const options = {
    stages: [
        { duration: '1m', target: 20 },
        { duration: '1m', target: 50 },
        { duration: '1m', target: 100 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.05'],
    },
};

export function setup() {
    const theaterRes = http.get(`${BASE_URL}/api/theater`);
    const theaterId = theaterRes.json().theater[0].id;

    const movieRes = http.get(`${BASE_URL}/api/movie`);
    const movieId = movieRes.json().searchedMovies[0].id;

    const screenRes = http.get(
        `${BASE_URL}/api/screen?theaterId=${theaterId}&movieId=${movieId}&date=2026-05-28`
    );
    const screeningId = screenRes.json().screen[0].screening[0].id;

    const loginRes = http.post(
        `${BASE_URL}/api/login`,
        JSON.stringify({ loginId: 'ceos1234', password: 'ceos1234**' }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    const token = loginRes.cookies.accessToken[0].value;

    return { screeningId, token };
}

export default function (data) {

    // ✅ 매 요청마다 쿠키 헤더에 직접 박기
    const headers = {
        'Content-Type': 'application/json',
        'Cookie': `accessToken=${data.token}`,
    };

    const base = ((__VU - 1) * 2 + __ITER * 2) % 171 + 1;

    // ----------------------
    // 1. 예매
    // ----------------------
    const reserveRes = http.post(
        `${BASE_URL}/api/reservation/seats`,
        JSON.stringify({
            screeningId: data.screeningId,
            seatInfos: [
                { seatName: base, info: 'ADULT' },
                { seatName: base + 1, info: 'ADULT' },
            ],
        }),
        { headers }
    );

    check(reserveRes, { 'reserve success': (r) => r.status === 200 });

    if (reserveRes.status !== 200) {
        console.log(`[VU ${__VU}][ITER ${__ITER}] reserve failed: ${reserveRes.status} ${reserveRes.body}`);
        sleep(1);
        return;
    }

    const reservationId = reserveRes.json().id;
    console.log(`[VU ${__VU}][ITER ${__ITER}] reserved id=${reservationId} seat=${base},${base+1}`);

    sleep(1);

    // ----------------------
    // 2. 결제
    // ----------------------
    const payRes = http.post(
        `${BASE_URL}/api/reservation?reservationId=${reservationId}`,
        null,
        { headers }
    );

    check(payRes, { 'pay success': (r) => r.status === 200 });

    if (payRes.status !== 200) {
        console.log(`[VU ${__VU}][ITER ${__ITER}] pay failed: ${payRes.status} ${payRes.body}`);
        sleep(1);
        return;
    }

    console.log(`[VU ${__VU}][ITER ${__ITER}] paid id=${reservationId}`);

    sleep(1);

    // ----------------------
    // 3. 취소
    // ----------------------
    const cancelRes = http.del(
        `${BASE_URL}/api/reservation/${reservationId}`,
        null,
        { headers }
    );

    check(cancelRes, { 'cancel success': (r) => r.status === 200 });

    console.log(`[VU ${__VU}][ITER ${__ITER}] cancelled id=${reservationId}`);

    sleep(1);
}