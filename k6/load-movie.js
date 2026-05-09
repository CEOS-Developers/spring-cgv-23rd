import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';

export const options = {
    stages: [
        { duration: '1m', target: 200 },  // 1분 동안 200명까지 증가
        { duration: '2m', target: 500 },  // 2분 동안 500명까지 증가
        { duration: '1m', target: 0 },    // 쿨다운
    ],
};

export default function () {
    const res = http.get(`${BASE_URL}/movies`);

    check(res, {
        'is status 200': (r) => r.status === 200,
        'has movies': (r) => r.json('data').length > 0,
    });

    sleep(1);
}