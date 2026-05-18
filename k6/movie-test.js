import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 20 },
        { duration: '30s', target: 50 },
        { duration: '30s', target: 100 },
        { duration: '30s', target: 150 },
        { duration: '30s', target: 200 },
        { duration: '30s', target: 0 },
    ],
};

const BASE_URL = __ENV.BASE_URL;
const TOKEN = __ENV.TOKEN;

if (!BASE_URL) {
    throw new Error('BASE_URL 환경변수가 필요합니다. 예: k6 run -e BASE_URL=http://서버주소 k6/movie-test.js');
}

export default function () {
    const res = http.get(`${BASE_URL}/api/movies`, {
        headers: {
            Authorization: `Bearer ${TOKEN}`,
        },
    });

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}