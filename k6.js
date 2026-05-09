import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080';

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

// ----------------------
// setup
// ----------------------
export function setup() {

    const theaterRes = http.get(`${BASE_URL}/api/theater`);
    const theaterId = theaterRes.json().theater[0].id;

    const movieRes = http.get(`${BASE_URL}/api/movie`);
    const movieId = movieRes.json().searchedMovies[0].id;

    const screenRes = http.get(
        `${BASE_URL}/api/screen?theaterId=${theaterId}&movieId=${movieId}&date=2026-05-28`
    );

    const screeningId =
        screenRes.json().screen[0].screening[0].id;

    return {
        screeningId,
    };
}

// ----------------------
// VU별 로그인 상태
// ----------------------
let loggedIn = false;

// ----------------------
// 테스트 실행
// ----------------------
export default function (data) {

    // 최초 1회 로그인
    if (!loggedIn) {

        const loginRes = http.post(
            `${BASE_URL}/api/login`,
            JSON.stringify({
                loginId: 'ceos1234',
                password: 'ceos1234**',
            }),
            {
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        );
        console.log(loginRes.status);

        check(loginRes, {
            'login success': (r) =>
                r.status === 200 ||
                r.status === 201 ||
                r.status === 204,
        });

        // 로그인 성공한 경우만 유지
        if (
            loginRes.status === 200 ||
            loginRes.status === 201 ||
            loginRes.status === 204
        ) {
            loggedIn = true;
        } else {
            return;
        }
    }

    // 좌석 분산
    const seat1 = ((__VU * 2 + __ITER) % 172) + 1;
    const seat2 = ((__VU * 2 + __ITER + 1) % 172) + 1;

    const payload = {
        screeningId: data.screeningId,
        seatInfos: [
            { seatId: seat1 },
            { seatId: seat2 },
        ],
    };

    const res = http.post(
        `${BASE_URL}/api/reservations`,
        JSON.stringify(payload),
        {
            headers: {
                'Content-Type': 'application/json',
            },
        }
    );

    check(res, {

        // 서버 장애 여부
        'server alive': (r) => r.status < 500,

        // 예약 성공
        'reservation success': (r) =>
            r.status === 200 || r.status === 201,

        // 좌석 충돌 정상 처리
        'seat conflict handled': (r) =>
            [200, 201, 400, 409].includes(r.status),
    });

    sleep(1);
}