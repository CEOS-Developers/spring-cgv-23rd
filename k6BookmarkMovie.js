import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080';

export const options = {
    stages: [
        { duration: '30s', target: 300 },
        { duration: '30s', target: 500 },
        { duration: '1m', target: 1000 },
        { duration: '1m', target: 3000 },
        { duration: '30s', target: 0 },
    ],

    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.05'],
    },
};

export default function () {

    let movieId;

    // 80% 확률로 인기 영화 2개에 집중
    const random = Math.random();

    if (random < 0.4) {
        // 영화 1
        movieId = 1;

    } else if (random < 0.8) {
        // 영화 2
        movieId = 2;

    } else {
        movieId = Math.floor(Math.random() * 9998) + 3;
    }

    const res = http.get(
        `${BASE_URL}/api/movie/${movieId}`
    );

    if (res.status !== 200) {
        console.log(res.status);
        console.log(res.body);
    }

    check(res, {
        'movie detail success': (r) => r.status === 200,
    });

    sleep(0.3);
}