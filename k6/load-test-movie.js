import http from "k6/http";
import {check, group, sleep} from "k6";

export const options = {
    stages: [
        {duration: "2m", target: 100}, // 0~2분 동안 VU 0->100명
        {duration: "2m", target: 200},
    ],
    thresholds: {
        http_req_duration: ["p(95)<500"],
        http_req_failed: ["rate<0.05"],

        'http_req_duration{group:::1. 영화 차트 조회}': ['p(95)<500'],
        'http_req_duration{group:::2. 현재 상영작 조회}': ['p(95)<500'],
        'http_req_duration{group:::3. 영화 상세 조회}': ['p(95)<500'],
        'http_req_duration{group:::4. 영화 출연진 조회}': ['p(95)<500'],
    },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

export function setup() {
    const res = http.post(`${BASE_URL}/api/v1/auth/guest`);
    check(res, {"guest token issued": (r) => r.status === 200});

    const cookies = res.headers["Set-Cookie"];
    if (!cookies) throw new Error("게스트 토큰 발급 실패");

    const cookieArray = Array.isArray(cookies) ? cookies : [cookies];
    let accessToken = "";
    for (const c of cookieArray) {
        if (c.includes("access_token=")) {
            accessToken = c.split("access_token=")[1].split(";")[0];
            break;
        }
    }
    if (!accessToken) throw new Error("accessToken 쿠키를 찾을 수 없습니다.");
    return {accessToken};
}

export default function (data) {
    const jar = http.cookieJar();
    jar.set(BASE_URL, "access_token", data.accessToken);

    group('1. 영화 차트 조회', function () {
        const chartRes = http.get(`${BASE_URL}/api/v1/movies/chart`);
        check(chartRes, {"[Chart] status 200": (r) => r.status === 200});
    });
    sleep(0.5);

    group('2. 현재 상영작 조회', function () {
        const runningRes = http.get(`${BASE_URL}/api/v1/movies/running`);
        check(runningRes, {"[Running] status 200": (r) => r.status === 200});
    });
    sleep(0.5);

    // 영화 상세 조회
    group('3. 영화 상세 조회', function () {
        const movieId = 1;
        const detailRes = http.get(`${BASE_URL}/api/v1/movies/${movieId}`);
        check(detailRes, {"[Detail] status 200": (r) => r.status === 200});
    })
    sleep(0.5);

    group('4. 영화 출연진 조회', function () {
        const movieId = 1;
        const creditsRes = http.get(`${BASE_URL}/api/v1/movies/${movieId}/credits`);
        check(creditsRes, {"[Credit] status 200": (r) => r.status === 200});
    })
    sleep(1);
}