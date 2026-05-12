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

        'http_req_duration{group:::1. 극장 목록 조회}': ['p(95)<500'],
        'http_req_duration{group:::2. 극장 상세 조회}': ['p(95)<500'],
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

    // 극장 목록 조회

    group('1. 극장 목록 조회', function () {
        const listRes = http.get(`${BASE_URL}/api/v1/theaters`);
        check(listRes, {"theaters list 200": (r) => r.status === 200});
    })
    sleep(0.5);

    // 극장 상세 조회
    group('2. 극장 상세 조회', function () {
        const theaterId = 1
        const detailRes = http.get(`${BASE_URL}/api/v1/theaters/${theaterId}`);
        check(detailRes, {"theater detail 200": (r) => r.status === 200});
    })
    sleep(1);
}