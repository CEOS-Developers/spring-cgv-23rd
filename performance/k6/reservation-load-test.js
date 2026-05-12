import http from 'k6/http';
import { Counter } from 'k6/metrics';
import { check, sleep } from 'k6';

const SMOKE = (__ENV.SMOKE || 'false').toLowerCase() === 'true';
const TEST_LEVEL = __ENV.TEST_LEVEL || 'small';
const TEST_MODE = __ENV.TEST_MODE || 'payment';
const DEBUG = (__ENV.DEBUG || 'false').toLowerCase() === 'true';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const LOGIN_ID = __ENV.LOGIN_ID || 'testuser';
const LOGIN_PASSWORD = __ENV.LOGIN_PASSWORD || 'password123!';

const SCHEDULE_IDS = parseScheduleIds(__ENV.SCHEDULE_IDS || __ENV.SCHEDULE_ID || '1');

const SEAT_ROWS = (__ENV.SEAT_ROWS || 'A,B,C,D')
    .split(',')
    .map((row) => row.trim())
    .filter(Boolean);

const SEAT_COLS = Number(__ENV.SEAT_COLS || 10);

const TEST_CONFIG = getTestConfig(TEST_LEVEL);
const RUN_CANCEL = (__ENV.RUN_CANCEL || 'false').toLowerCase() === 'true';

const reservedSeatConflicts = new Counter('reserved_seat_conflicts');
const reservationAttempts = new Counter('reservation_attempts');

export const options = SMOKE
    ? getSmokeOptions()
    : getLoadOptions(TEST_CONFIG);

export function setup() {
    const token = login();

    console.log(`BASE_URL=${BASE_URL}`);
    console.log(`TEST_LEVEL=${TEST_LEVEL}`);
    console.log(`TEST_MODE=${TEST_MODE}`);
    console.log(`SMOKE=${SMOKE}`);
    console.log(`SCHEDULE_IDS=${SCHEDULE_IDS.join(',')}`);
    console.log(`SEAT_ROWS=${SEAT_ROWS.join(',')}`);
    console.log(`SEAT_COLS=${SEAT_COLS}`);
    console.log(`MAX_VUS=${TEST_CONFIG.maxVus}`);
    console.log(`RUN_CANCEL=${RUN_CANCEL}`);
    console.log(`DEBUG=${DEBUG}`);

    return { token };
}

export default function (data) {
    const target = pickReservationTarget(__VU, __ITER);

    const reservationId = reserve(data.token, target.scheduleId, target.seat);

    if (!reservationId) {
        sleep(1);
        return;
    }

    pay(data.token, reservationId);

    if (RUN_CANCEL) {
        cancel(data.token, reservationId);
    }

    sleep(1);
}

function getTestConfig(level) {
    const configs = {
        small: {
            maxVus: 10,
            stages: [
                { duration: '15s', target: 5 },
                { duration: '15s', target: 10 },
                { duration: '15s', target: 0 },
            ],
        },
        medium: {
            maxVus: 50,
            stages: [
                { duration: '30s', target: 20 },
                { duration: '1m', target: 50 },
                { duration: '1m', target: 50 },
                { duration: '30s', target: 0 },
            ],
        },
        large: {
            maxVus: 100,
            stages: [
                { duration: '2m', target: 50 },
                { duration: '3m', target: 100 },
                { duration: '3m', target: 100 },
                { duration: '2m', target: 0 },
            ],
        },
    };

    return configs[level] || configs.small;
}

function getSmokeOptions() {
    return {
        vus: 1,
        iterations: 1,
        thresholds: commonThresholds(),
    };
}

function getLoadOptions(config) {
    return {
        stages: config.stages,
        thresholds: commonThresholds(),
    };
}

function commonThresholds() {
    const thresholds = {
        http_req_duration: ['p(95)<1500'],
        'checks{type:login}': ['rate>0.99'],
        'checks{type:reserve_attempt}': ['rate>0.95'],
    };

    if (TEST_MODE === 'payment') {
        thresholds['checks{type:pay}'] = ['rate>0.90'];
    }

    return thresholds;
}

function login() {
    const response = http.post(
        `${BASE_URL}/api/users/login`,
        JSON.stringify({
            loginId: LOGIN_ID,
            password: LOGIN_PASSWORD,
        }),
        {
            headers: { 'Content-Type': 'application/json' },
            tags: { type: 'login' },
        }
    );

    const success = check(
        response,
        {
            'login status is 200': (r) => r.status === 200,
            'login token exists': (r) => Boolean(safeJson(r)?.data?.accessToken),
        },
        { type: 'login' }
    );

    if (!success) {
        throw new Error(`login failed: status=${response.status}, body=${response.body}`);
    }

    return safeJson(response).data.accessToken;
}

function reserve(token, scheduleId, seat) {
    reservationAttempts.add(1);

    const response = http.post(
        `${BASE_URL}/api/reservations`,
        JSON.stringify({
            scheduleId,
            seats: [seat],
        }),
        {
            headers: authHeaders(token),
            tags: { type: 'reserve' },
        }
    );

    const conflict = isReservedSeatConflict(response);

    if (conflict) {
        reservedSeatConflicts.add(1);

        if (DEBUG) {
            console.warn(
                `reserved seat conflict: scheduleId=${scheduleId}, seat=${seat.seatRow}${seat.seatCol}, status=${response.status}`
            );
        }
    }

    check(
        response,
        {
            'reserve attempt handled': (r) => r.status === 200 || isReservedSeatConflict(r),
        },
        { type: 'reserve_attempt' }
    );

    const success = check(
        response,
        {
            'reserve status is 200': (r) => r.status === 200,
            'reservation id exists': (r) => typeof safeJson(r)?.data === 'number',
        },
        { type: 'reserve_success' }
    );

    if (!success) {
        if (DEBUG && !conflict) {
            console.error(
                `reserve failed: scheduleId=${scheduleId}, seat=${seat.seatRow}${seat.seatCol}, status=${response.status}, body=${response.body}`
            );
        }

        return null;
    }

    return safeJson(response).data;
}

function pay(token, reservationId) {
    const response = http.post(
        `${BASE_URL}/api/reservations/${reservationId}/pay`,
        null,
        {
            headers: authHeaders(token),
            tags: { type: 'pay' },
        }
    );

    const success = check(
        response,
        {
            'pay status is 200': (r) => r.status === 200,
            'payment marked paid': (r) => {
                const body = safeJson(r);

                return (
                    body?.data?.paymentStatus === 'PAID' ||
                    body?.data?.status === 'PAID' ||
                    body?.paymentStatus === 'PAID' ||
                    body?.status === 'PAID'
                );
            },
        },
        { type: 'pay' }
    );

    if (!success && DEBUG) {
        console.error(
            `pay failed: reservationId=${reservationId}, status=${response.status}, body=${response.body}`
        );
    }
}

function cancel(token, reservationId) {
    const response = http.patch(
        `${BASE_URL}/api/reservations/${reservationId}/cancel`,
        null,
        {
            headers: authHeaders(token),
            tags: { type: 'cancel' },
        }
    );

    check(
        response,
        {
            'cancel status is 200': (r) => r.status === 200,
        },
        { type: 'cancel' }
    );

    if (response.status !== 200 && DEBUG) {
        console.error(
            `cancel failed: reservationId=${reservationId}, status=${response.status}, body=${response.body}`
        );
    }
}

function authHeaders(token) {
    return {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
    };
}

function pickReservationTarget(vu, iter) {
    if (TEST_MODE === 'conflict') {
        return pickConflictTarget();
    }

    return pickPaymentTarget(vu, iter);
}

function pickPaymentTarget(vu, iter) {
    const sequence = getSequence(vu, iter);

    const scheduleIndex = sequence % SCHEDULE_IDS.length;
    const scheduleId = SCHEDULE_IDS[scheduleIndex];

    const totalSeats = SEAT_ROWS.length * SEAT_COLS;
    const seatIndex = Math.floor(sequence / SCHEDULE_IDS.length) % totalSeats;

    return {
        scheduleId,
        seat: toSeat(seatIndex),
    };
}

function pickConflictTarget() {
    return {
        scheduleId: SCHEDULE_IDS[0],
        seat: {
            seatRow: SEAT_ROWS[0],
            seatCol: 1,
        },
    };
}

function getSequence(vu, iter) {
    return iter * TEST_CONFIG.maxVus + (vu - 1);
}

function toSeat(seatIndex) {
    const rowIndex = Math.floor(seatIndex / SEAT_COLS);
    const col = (seatIndex % SEAT_COLS) + 1;

    return {
        seatRow: SEAT_ROWS[rowIndex],
        seatCol: col,
    };
}

function parseScheduleIds(rawValue) {
    const scheduleIds = rawValue
        .split(',')
        .map((value) => Number(value.trim()))
        .filter((value) => Number.isInteger(value) && value > 0);

    return scheduleIds.length > 0 ? scheduleIds : [1];
}

function isReservedSeatConflict(response) {
    if (response.status !== 400 && response.status !== 409) {
        return false;
    }

    const body = response.body || '';

    return (
        body.includes('이미 예약된 좌석') ||
        body.includes('이미 선점된 좌석') ||
        body.includes('reserved') ||
        body.includes('conflict')
    );
}

function safeJson(response) {
    try {
        return response.json();
    } catch (e) {
        return null;
    }
}