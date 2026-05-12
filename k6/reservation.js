import http from 'k6/http';
import { check, sleep, fail } from 'k6';

const BASE = __ENV.BASE_URL;
const USER_COUNT = Number(__ENV.USER_COUNT || 300);
const SCHEDULE_IDS = (__ENV.SCHEDULE_IDS || '1').split(',').map(Number);
const PASSWORD = 'Test1234!';

const ITERS_PER_VU = 200;
const MAX_COL = 300;

export const options = {
    stages: [
        { duration: '30s', target: 20 },
        { duration: '30s', target: 50 },
        { duration: '30s', target: 100 },
        { duration: '30s', target: 200 },
        { duration: '30s', target: 300 },
        { duration: '30s', target: 0 },
    ],
};

function signupOrLogin(email) {
    const body = JSON.stringify({ email, password: PASSWORD });
    const headers = { 'Content-Type': 'application/json' };

    let res = http.post(`${BASE}/api/users/signup`, body, { headers });
    if (res.status !== 200) {
        res = http.post(`${BASE}/api/users/login`, body, { headers });
    }
    if (res.status !== 200) {
        fail(`auth failed for ${email}: ${res.status} ${res.body}`);
    }
    return res.json('result.accessToken');
}

function pickSeat(vu, iter) {
    const vuPerSched = Math.floor((vu - 1) / SCHEDULE_IDS.length);
    const slot = vuPerSched * ITERS_PER_VU + iter;
    const col = (slot % MAX_COL) + 1;
    const row = String.fromCharCode('A'.charCodeAt(0) + Math.floor(slot / MAX_COL));
    return `${row}${col}`;
}

export function setup() {
    const tokens = [];
    for (let i = 1; i <= USER_COUNT; i++) {
        const email = `loadtest${i}@k6.local`;
        tokens.push(signupOrLogin(email));
    }
    console.log(`prepared ${tokens.length} tokens, schedules=${SCHEDULE_IDS}`);
    return { tokens };
}

export default function (data) {
    const token = data.tokens[(__VU - 1) % data.tokens.length];
    const headers = { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' };

    const scheduleId = SCHEDULE_IDS[(__VU - 1) % SCHEDULE_IDS.length];
    const seat = pickSeat(__VU, __ITER);

    const prep = http.post(`${BASE}/api/reservations`, JSON.stringify({
        scheduleId,
        seats: [seat],
    }), { headers, tags: { name: 'prepare' } });

    const ok = check(prep, { 'prepare 200': (r) => r.status === 200 });
    if (!ok) { sleep(1); return; }

    const id = prep.json('result.reservationId');
    if (!id) { sleep(1); return; }

    const pay = http.post(`${BASE}/api/reservations/${id}/pay`, null,
        { headers, tags: { name: 'pay' } });
    check(pay, { 'pay 200': (r) => r.status === 200 });

    sleep(1);
}
