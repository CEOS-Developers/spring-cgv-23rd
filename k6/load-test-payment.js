import http from "k6/http";
import { check, sleep } from "k6";

/*
 *	k6에게 이 부하테스트를 어떤 패턴으로 돌릴 거다를 알려주는 시나리오 설정
 *	stages 안의 각 항목 : duration 동안 동시 접속자 수(VU, Virtual User)를
 *										 target까지 점진적으로 늘려라
 */
export const options = {
  stages: [
    { duration: "2m", target: 100 }, // 0~2분 동안 VU 0->100명
    { duration: "2m", target: 200 },
  ],
};

const BASE_URL = "https://ceos.diggindie.com"; // 과제 결제 서버 Base_URL
const GITHUB_ID = "fervovita"; // 예) 'Hoyoung027'

// 1. 가맹점 API Secret 조회 (테스트 시작 시 1회 실행)
export function setup() {
  const res = http.get(`${BASE_URL}/auth/${GITHUB_ID}`, {
    headers: { "Content-Type": "application/json" },
  });

  check(res, {
    "auth status is 200": (r) => r.status === 200,
  });

  const body = res.json();

  const apiSecretKey = body && body.payload && body.payload.apiSecretKey;

  if (!apiSecretKey) {
    throw new Error(
      `API Secret 발급 실패: status=${res.status}, body=${res.body}`,
    );
  }

  return { apiSecretKey };
}

// 2. 결제 요청 + 3. 결제 조회 (각 VU가 무한 반복)
export default function (data) {
  // 2. 결제 요청 API 호출
  const headers = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${data.apiSecretKey}`,
  };

  const paymentId = `order_${Date.now()}_${__VU}_${__ITER}`;
  // ex) order_1746230400123_1_0

  const paymentPayload = JSON.stringify({
    storeId: GITHUB_ID,
    orderName: "노트북외 1건",
    totalPayAmount: 1500000,
    currency: "KRW",
    customData: JSON.stringify({ item: "노트북", quantity: 1 }),
  });

  const paymentRes = http.post(
    `${BASE_URL}/payments/${paymentId}/instant`,
    paymentPayload,
    { headers },
  );

  // 2-1. 결제 요청 API 응답 검증
  check(paymentRes, {
    "payment status is 200": (r) => r.status === 200,
  });

  // 3. 결제 조회 API 호출 (결제 성공 시에만)
  if (paymentRes.status === 200) {
    /*
     * sleep(n) : k6에게 n초 동안 아무것도 하지 말라고 시키는 명령
     */
    sleep(1); // 결제 성공 후 -> 조회 사이 1초 대기

    const lookupRes = http.get(`${BASE_URL}/payments/${paymentId}`, {
      headers,
    });

    check(lookupRes, {
      "lookup status is 200": (r) => r.status === 200,
    });
  }

  sleep(1); // 한 사이클(결제+조회) 끝나고 다음 반복 전 1초 대기
}
