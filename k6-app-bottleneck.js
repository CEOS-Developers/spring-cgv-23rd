import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'https://ceos.diggindie.com';
const USER_EMAIL = __ENV.USER_EMAIL || 'string';
const USER_PASSWORD = __ENV.USER_PASSWORD || 'string';
const THEATER_ID = Number(__ENV.THEATER_ID || 1);
const FOOD_ID = Number(__ENV.FOOD_ID || 1);
const QUANTITY = Number(__ENV.QUANTITY || 1);
const ORDER_PAGE = Number(__ENV.ORDER_PAGE || 0);
const ORDER_SIZE = Number(__ENV.ORDER_SIZE || 20);
const MODE = __ENV.MODE || 'full_payment';

function buildOptions() {
  if (MODE === 'db_only') {
    return {
      stages: [
        { duration: '2m', target: 10 },
        { duration: '2m', target: 30 },
      ],
      thresholds: {
        'http_req_failed{name:create_food_order}': ['rate<0.05'],
        'http_req_duration{name:create_food_order}': ['p(95)<1000'],
        'http_req_duration{name:get_food_orders}': ['p(95)<700'],
      },
    };
  }

  return {
    stages: [
      { duration: '2m', target: 100 },
      { duration: '2m', target: 200 },
    ],
    thresholds: {
      'http_req_failed{name:create_food_order}': ['rate<0.05'],
      'http_req_failed{name:process_food_payment}': ['rate<0.10'],
      'http_req_duration{name:process_food_payment}': ['p(95)<3000'],
    },
  };
}

export const options = buildOptions();

export function setup() {
  const loginPayload = JSON.stringify({
    email: USER_EMAIL,
    password: USER_PASSWORD,
  });

  const loginRes = http.post(`${BASE_URL}/api/auth/login`, loginPayload, {
    headers: { 'Content-Type': 'application/json' },
    tags: { name: 'login' },
  });

  check(loginRes, {
    'login status is 200': (r) => r.status === 200,
    'login has access token': (r) => !!r.json('result.accessToken'),
  });

  const accessToken = loginRes.json('result.accessToken');

  if (!accessToken) {
    throw new Error(`로그인 실패: status=${loginRes.status}, body=${loginRes.body}`);
  }

  return { accessToken };
}

function authHeaders(token) {
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };
}

function createFoodOrder(token) {
  const payload = JSON.stringify({
    theaterId: THEATER_ID,
    items: [
      {
        foodId: FOOD_ID,
        quantity: QUANTITY,
      },
    ],
  });

  const res = http.post(`${BASE_URL}/api/foods/orders/`, payload, {
    headers: authHeaders(token),
    tags: { name: 'create_food_order' },
  });

  check(res, {
    'create food order status is 200': (r) => r.status === 200,
    'create food order has order id': (r) => !!r.json('result'),
  });

  return res;
}

function getFoodOrders(token) {
  return http.get(`${BASE_URL}/api/foods/orders/?page=${ORDER_PAGE}&size=${ORDER_SIZE}`, {
    headers: authHeaders(token),
    tags: { name: 'get_food_orders' },
  });
}

function processFoodPayment(token, orderId) {
  return http.post(`${BASE_URL}/api/foods/orders/${orderId}/payments`, null, {
    headers: authHeaders(token),
    tags: { name: 'process_food_payment' },
  });
}

export default function (data) {
  const token = data.accessToken;

  if (MODE === 'db_only') {
    const createRes = createFoodOrder(token);
    const listRes = getFoodOrders(token);

    check(listRes, {
      'get food orders status is 200': (r) => r.status === 200,
    });

    if (createRes.status !== 200) {
      return;
    }

    sleep(1);
    return;
  }

  const createRes = createFoodOrder(token);
  if (createRes.status !== 200) {
    sleep(1);
    return;
  }

  const orderId = createRes.json('result');
  const paymentRes = processFoodPayment(token, orderId);

  check(paymentRes, {
    'process food payment status is 200': (r) => r.status === 200,
  });

  sleep(1);
}
