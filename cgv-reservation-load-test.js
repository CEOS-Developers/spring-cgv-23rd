import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

export const reservationCreated = new Rate('reservation_created');
export const expectedPaymentFailure = new Rate('expected_payment_failure');
export const seatConflict = new Rate('seat_conflict');

export const options = {
  scenarios: {
    reservation_ramp: {
      executor: 'ramping-vus',
      stages: [
        { duration: '5s', target: Number(__ENV.STAGE1_VUS || 1) },
        { duration: '2m', target: Number(__ENV.STAGE2_VUS || 30) },
        { duration: '2m', target: Number(__ENV.STAGE3_VUS || 50) },
        { duration: '1m', target: 0 },
      ],
      gracefulRampDown: '20s',
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    // CEOS 결제 서버가 확률적으로 실패할 수 있으므로, 실제 분석에서는 R006 비율을 따로 보기
    http_req_failed: ['rate<0.20'],
    reservation_created: ['rate>0.70'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const SCREENING_ID = Number(__ENV.SCREENING_ID || 1);
const PAYMENT = __ENV.PAYMENT || 'KAKAO_PAY';
const COUPON_CODE = __ENV.COUPON_CODE || null;
const SEAT_MODE = __ENV.SEAT_MODE || 'unique'; // unique | contention
const SEAT_POOL_SIZE = Number(__ENV.SEAT_POOL_SIZE || 1);
const SEAT_OFFSET = Number(__ENV.SEAT_OFFSET || Math.floor(Math.random() * 1000000));

function jsonHeaders(extra = {}) {
  return { headers: { 'Content-Type': 'application/json', ...extra } };
}

function safeJson(res) {
  try {
    return res.json();
  } catch (_) {
    return {};
  }
}

function makeSeat() {
  if (SEAT_MODE === 'contention') {
    return `A${(__ITER % SEAT_POOL_SIZE) + 1}`;
  }

  return `A${SEAT_OFFSET + ((__VU - 1) * 100000) + __ITER + 1}`;
}

export function setup() {
  let email = __ENV.EMAIL;
  let password = __ENV.PASSWORD || 'Password1234!';

  if (__ENV.SIGNUP === 'true') {
    const suffix = `${Date.now()}-${Math.floor(Math.random() * 100000)}`;
    email = email || `k6-${suffix}@example.com`;
    const signupPayload = JSON.stringify({
      name: `k6-user-${suffix}`,
      email,
      nickname: `k6-${suffix}`,
      password,
    });

    const signupRes = http.post(`${BASE_URL}/api/auth/signup`, signupPayload, jsonHeaders());
    check(signupRes, {
      'signup 201 or duplicate 409': (r) => r.status === 201 || r.status === 409,
    });
  }

  if (!email) {
    throw new Error('EMAIL env가 필요합니다. 새 계정을 만들려면 SIGNUP=true를 함께 지정하세요.');
  }

  const loginRes = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({ email, password }),
    jsonHeaders(),
  );

  check(loginRes, { 'login status is 200': (r) => r.status === 200 });
  const body = safeJson(loginRes);
  const accessToken = body?.data?.accessToken;

  if (!accessToken) {
    throw new Error(`로그인 실패: status=${loginRes.status}, body=${loginRes.body}`);
  }

  return { accessToken };
}

export default function (data) {
  const seat = makeSeat();
  const payload = JSON.stringify({
    screeningId: SCREENING_ID,
    payment: PAYMENT,
    couponCode: COUPON_CODE,
    seatNumbers: [seat],
  });

  if (__ITER < 3 && __VU === 1) {
    console.log(`payload=${payload}`);
  }

  const res = http.post(
    `${BASE_URL}/api/reservations`,
    payload,
    jsonHeaders({ Authorization: `Bearer ${data.accessToken}` }),
  );

  if (res.status !== 201) {
    console.log(`status=${res.status}, body=${res.body}`);
  }

  const body = safeJson(res);
  const code = body?.code;

  const created = res.status === 201;
  const paymentFailed = res.status === 502 && code === 'R006';
  const duplicatedSeat = res.status === 409 && code === 'R002';

  reservationCreated.add(created);
  expectedPaymentFailure.add(paymentFailed);
  seatConflict.add(duplicatedSeat);

  check(res, {
    'reservation created': () => created,
    'payment failure is classified as R006': () => !paymentFailed || code === 'R006',
    'seat conflict is classified as R002': () => !duplicatedSeat || code === 'R002',
  });

  sleep(Number(__ENV.THINK_TIME || 1));
}
