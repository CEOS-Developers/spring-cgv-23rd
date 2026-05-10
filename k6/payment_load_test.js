import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// ────────────────────────────────────────────
// 환경 설정
// ────────────────────────────────────────────
const BASE_URL = __ENV.BASE_URL || 'http://localhost';

const TEST_EMAIL    = __ENV.TEST_EMAIL    || 'loadtest@example.com';
const TEST_PASSWORD = __ENV.TEST_PASSWORD || 'Test1234!';

// MasterDataInitializer 기준:
//   Schedule 1 = theater1(CGV 강남점) × STANDARD관 × 듄
//   ECONOMY 좌석(A~E열 × 20개) = ID 1~100, basePrice=15000, surcharge=0
const SCHEDULE_ID = parseInt(__ENV.SCHEDULE_ID || '1');
const SEAT_IDS    = __ENV.SEAT_IDS
  ? __ENV.SEAT_IDS.split(',').map(Number)
  : Array.from({ length: 100 }, (_, i) => i + 1); // 1~100

// ────────────────────────────────────────────
// 커스텀 메트릭
// ────────────────────────────────────────────
const paymentSuccess   = new Counter('payment_success_total');
const paymentFailed    = new Counter('payment_failed_total');
const paymentErrorRate = new Rate('payment_error_rate');
const paymentDuration  = new Trend('payment_duration_ms', true);

// ────────────────────────────────────────────
// 부하 시나리오
// ────────────────────────────────────────────
export const options = {
  stages: [
    { duration: '30s', target: 10  },  // 워밍업
    { duration: '1m',  target: 30  },  // 부하 증가
    { duration: '2m',  target: 30  },  // 정상 상태 유지
    { duration: '30s', target: 50  },  // 스파이크
    { duration: '30s', target: 0   },  // 종료
  ],
  thresholds: {
    'payment_error_rate':          ['rate<0.05'],   // 에러율 5% 미만
    'payment_duration_ms':         ['p(95)<3000'],  // 95%ile 3초 이내
    'http_req_duration{url:payment}': ['p(99)<5000'],
  },
};

// ────────────────────────────────────────────
// Setup: 1회 로그인 → 토큰 공유
// ────────────────────────────────────────────
export function setup() {
  // 테스트 계정이 없으면 먼저 회원가입
  http.post(
    `${BASE_URL}/api/auth/signup`,
    JSON.stringify({ email: TEST_EMAIL, password: TEST_PASSWORD, name: 'LoadTester' }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  const loginRes = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({ email: TEST_EMAIL, password: TEST_PASSWORD }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(loginRes, { 'setup: login 200': (r) => r.status === 200 });

  const token = loginRes.json('accessToken');
  if (!token) {
    throw new Error(`로그인 실패: ${loginRes.status} ${loginRes.body}`);
  }
  return { token };
}

// ────────────────────────────────────────────
// 메인 VU 루프
// ────────────────────────────────────────────
export default function (data) {
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.token}`,
  };

  // VU·iteration 조합으로 겹치지 않는 seatId 선택
  const seatIndex = (__VU * 100 + __ITER) % SEAT_IDS.length;
  const seatId    = SEAT_IDS[seatIndex];

  // 결제 고유 ID: timestamp + VU + iter
  const paymentId = `${Date.now()}_vu${__VU}_i${__ITER}`;

  // ── 결제 API 호출 ──────────────────────────
  const paymentRes = http.post(
    `${BASE_URL}/api/reservations/instant`,
    JSON.stringify({
      scheduleId:     SCHEDULE_ID,
      seatIds:        [seatId],
      expectedAmount: 15000,
      paymentId:      paymentId,
    }),
    {
      headers,
      tags: { url: 'payment' },
    }
  );

  const ok = check(paymentRes, {
    'payment: status 200': (r) => r.status === 200,
    'payment: has paymentId': (r) => {
      try { return !!r.json('paymentId'); } catch { return false; }
    },
  });

  paymentDuration.add(paymentRes.timings.duration);
  paymentErrorRate.add(!ok);

  if (ok) {
    paymentSuccess.add(1);

    // ── 결제 취소 (좌석 해제 → 다음 VU가 재사용 가능) ─
    const cancelRes = http.post(
      `${BASE_URL}/api/reservations/${paymentId}/cancel`,
      null,
      { headers, tags: { url: 'cancel' } }
    );
    check(cancelRes, { 'cancel: status 200': (r) => r.status === 200 });

  } else {
    paymentFailed.add(1);
    // 실패 로그 (k6 출력에 표시됨)
    if (__ENV.VERBOSE === 'true') {
      console.warn(`[VU${__VU}] payment failed ${paymentRes.status}: ${paymentRes.body}`);
    }
  }

  sleep(1);
}

// ────────────────────────────────────────────
// Teardown
// ────────────────────────────────────────────
export function teardown(data) {
  console.log('부하 테스트 완료');
}
